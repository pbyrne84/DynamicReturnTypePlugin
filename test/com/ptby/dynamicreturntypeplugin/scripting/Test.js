var listener;

function test() {
    var impl = { run: function () { print('Hello, World!'); } };
    var runnable = new java.lang.Runnable(impl);
    listener.testCallBack( runnable );
}



 function TestObject(){
    this.b = function(){
        print( "a.b" );
        print( readFile( "./ScriptSignatureParserTest.java" ) );
    }
};


var a = new TestObject();