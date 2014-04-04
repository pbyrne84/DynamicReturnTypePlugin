var listener;

function test() {
    var impl = { run: function () { print('Hello, World!'); } };
    var runnable = new java.lang.Runnable(impl);
    listener.testCallBack( runnable );

    listener.runByInterface(
            function mooo( value ){
                print(value.length );

                print("running by interface");
            }
    );
}



 function TestObject(){
    this.b = function(){
        print( "a.b" );
    }
};


var a = new TestObject();