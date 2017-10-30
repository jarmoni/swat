$(document).ready(function() {
    var buffer = '';
    var port = parseInt(location.port) + 1;
    var ws = new WebSocket('ws://localhost:8080/name');

    function send(command) {
        // string to base64
    	var data = JSON.stringify({'command': command})
        ws.send(data);
    }

    function print(data) {
        // concatenate previous fragment
//        if (buffer) data = buffer + data;
//        // get data without last line
//        var arr = data.split('\n');
//        buffer = arr.pop();
//        data = arr.join('\n');
//        // decoding utf-8 chars
//        data = decodeURIComponent(escape(data));
        // print on terminal
        terminal.echo(data.data);
    }

    var terminal = $('#terminal').terminal(function(command, terminal) {
        send(command);
    }, {
        greetings: 'Welcome to websocket.sh terminal',
        prompt: '$ ',
        exit: false
    });
    window.terminal = terminal;

    ws.onopen = function() {
    	console.log('Socket open');
        terminal.enable();
    };
    ws.onclose = function() {
    	console.log('Socket closed');
        terminal.disable();
    };
    ws.onmessage = function(msg) {
    	console.log("MSG=" + msg);
        // convert base64 to string
        //var data = atob(msg.data);
        // show data
        print(msg);
    };
});
