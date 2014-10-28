DynamicReturnTypePlugin
=======================
( Jetbrains url http://plugins.jetbrains.com/plugin/7251 )

Intellij/Phpstorm PHP plugin to allow a dynamic return type from method/function calls. It can use either the instance type of the passed parameter or a string lookup.
Note: Use fully qualified names.

Example project
https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment

Uses
----
1. Generic like calls to simulate Phockito ( PHP Mockito) verify calls that return the object passed in.
eg. Phockito php example
```php
class DynamicReturnTypeTest extends PHPUnit_Framework_TestCase
{
    /** @var  Phockito */
    private $phockito;

    protected function setUp()
    {
        $this->phockito = new Phockito();
    }


    public function test_failsAsCallIsNotMade()
    {
        $domDocument = mock('\DomDocument');
        $newNode = mock('\DomElement');

        verify($domDocument)->appendChild($newNode);
    }

    public function test_succeedsAsCallIsMade()
    {
        $domDocument = mock('\DomDocument');
        $newNode = mock('\DomElement');

        $domDocument->appendChild($newNode);

        verify($domDocument)->appendChild($newNode);
    }


    public function test_staticPhockitoCall()
    {
        $domDocument = Phockito::mock('\DomDocument');
        $newNode =  Phockito::mock('\DomElement');

        $domDocument->appendChild($newNode);

        Phockito::verify( $domDocument )->appendChild($newNode);
    }

    public function test_instancePhockitoCall()
    {
        $domDocument = $this->phockito->mock('\DomDocument');
        $newNode =  $this->phockito->mock('\DomElement');

        $domDocument->appendChild($newNode);

        $this->phockito->verify( $domDocument )->appendChild($newNode);
    }
}
```

Configuration
-------------
Currently it looks for a files called dynamicReturnTypeMeta.json anywhere in the project. It also internally combines them to make a single project config. An example json file for the above test is

```json
{
    "methodCalls": [
        {
            "class": "\\Phockito",
            "method": "mock",
            "position": 0
        },
        {
            "class": "\\Phockito",
            "method": "verify",
            "position": 0
        },
    ],
    "functionCalls": [
        {
            "function": "\\verify",
            "position": 0
        },
        {
            "function": "\\mock",
            "position": 0
        }
    ]
}
```

Position is the parameter index that decides what will be the return type.


Optional Configuration
----------------------
There are currently 2 return type manipulation strategies.

### 1.Masks
These were the original replacement strategy but it has been found to be a rather limiting approach to the problem of frameworks containers expecting strings as parameters that do not easily match the final result without some string reduction/manipulation.

It simply works by using String.format on the the result.

For example the following configuration.
```json
{
"class": "\\Phockito",
"method": "maskExample",
"position": 0
"mask": "Test%sModel"
}
```

Would cause the following to return TestUserModel
```php
Phockito::maskExample('User');
```

There was no easy way to solve changing 'Entity\User' into 'MyNameSpace\User' without going down the path of writing a strategy for each custom user/framework request due to designs I have no knowledge of so cannot preempt. I would view this method as deprecated due to the limitations. But it will not be removed.

### 2.Script engine calls

#### NOTE: Script engine jar paths can now be set using IDEA_GROOVY_JAR_PATH and IDEA_JAVASCRIPT_JAR_PATH environment variables. IDEA_JAVASCRIPT_JAR_PATH is for the path to the nashorn.jar file for javascript in JDK8.

This allows custom code to be executed within the vm designed per consumer/framework. The 2 languages on offer are javascript via rhino and groovy if the groovy-all-2.2.1.jar is present in the ides lib folder. For interoperability between rhino and java the following reading is quite useful.

https://developer.mozilla.org/en-US/docs/Rhino/Scripting_Java

The benefit of using groovy is intellij idea(either edition) has better editing capabilities, though this requires setting up the IDEA_GROOVY_JAR_PATH path to groovy-all-2.2.1.jar (I set mine to point to the intellij ultimate jar).

To use groovy instead of javascript just use the .groovy extension (rhino is the default behaviour).

An example configuration

```json
{
    "class"                        : "\\Phockito",
    "method"                       : "javascriptMaskMock",
    "position"                     : 0,
    "fileReturnTypeReplacementCall": ["JavaScriptReplacementCallback.js", "replaceWithJavaScriptAliasing"]
}
```

Would cause the following to return Test_Foo_Model
```php
\\Phockito::javascriptMaskMock('Entity\User');
```

This manipulation causes the function 'replaceWithJavaScriptAliasing' to be called in JavaScriptReplacementCallback.js. The function needs to return a string and is formatted like the following example.

```js
function replaceWithJavaScriptAliasing( returnTypeNameSpace, returnTypeClass ){
    if( returnTypeNameSpace == 'Entity' ) {
        if( returnTypeClass == 'User' ) {
            return 'Test_Foo_Model';
        }else if( returnTypeClass == 'Test' ){
            return 'DynamicReturnTypePluginTestEnvironment\\TestClasses\\TestEntity';
        }
    }

    if( returnTypeNameSpace == '' ) {
        return returnTypeClass;

    }

    return returnTypeNameSpace + "\\" + returnTypeClass;


}
```

returnTypeNameSpace and returnTypeClass are separated before hand to ease manipulation within the function call. The returnTypeNameSpace trims the leading slash so \DomDocument is DomDocument.

#### Replacement callback file handling/compilation.
The only restriction is the script file must be in the the same folder as its related dynamicReturnTypeMeta.json. This restriction may be lifted at some point but it adds complexity.

Recompilation is triggered when a dynamicReturnTypeMeta.json is altered (say adding a space and pressing enter). This restriction will be removed now I can write compilation errors to the event log versus file log( basically compile on save will be offered).


#### Api
A variable call api is injected into the script which allows communication back to the ide. This can be expanded on request.
A javascript file that will offer completion can be found here :
https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment/blob/master/ExecutingScriptApi.js

Example initialisation can be seen here at the top :
https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment/blob/master/JavaScriptReplacementCallback.js


#### Debugging
```js
api.writeToEventLog("your message")
````

Will write a message to the event log in the ide. Errors in your script will also appear here.


### Notes/Todos
Unfortunately I cannot offer array access as the open api would have to alias the key to the offsetGet call internally which would trigger this plugin to be called. It was mentioned here in the task that started it all :-
http://youtrack.jetbrains.com/issue/WI-6027

I do plan to add file handling to the api so you can talk to the virtual file system for basic tasks and I will also see if I can open up some completion provider methods through the javascript api.

Currently IDEA_JAVASCRIPT_JAR_PATH is only for nashorn but rhino will be added for historical reasons.

Any probs just give me a shout.















