DynamicReturnTypePlugin
=======================
[![Version](http://phpstorm.espend.de/badge/7251/version)](https://plugins.jetbrains.com/plugin/7251)
[![Downloads](http://phpstorm.espend.de/badge/7251/downloads)](https://plugins.jetbrains.com/plugin/7251)
[![Downloads last month](http://phpstorm.espend.de/badge/7251/last-month)](https://plugins.jetbrains.com/plugin/7251)

( Jetbrains url https://plugins.jetbrains.com/plugin/7251 )

## Migration notice

A lot of this plugins functionality is now covered by PHPStorm itself

[https://www.jetbrains.com/help/phpstorm/ide-advanced-metadata.html](https://www.jetbrains.com/help/phpstorm/ide-advanced-metadata.html)

There is an example project here with some basic usage including using a mask so the string
**\Dom** becomes **\DomDocument** etc.

[https://github.com/pbyrne84/phpstorm-metadata-example](https://github.com/pbyrne84/phpstorm-metadata-example)

There is also an example of using PHPDoc for simple cases where an instance or a ::class reference is
being passed to something using the generic signature as exampled below :-

[https://phpstan.org/blog/generics-in-php-using-phpdocs](https://phpstan.org/blog/generics-in-php-using-phpdocs)

```php
/**
 * @template T
 *
 * @param T $a
 *
 * @return T
 */
function a($a) {
    return $a;
}
```

Truthfully if the concept of **::class** and the phpdoc syntax has both existed and were also supported by PHPStorm then I would not have written
this plugin. I wanted a php version of Mockito as the generic way is works is IDE friendly. The passing the method name as a string
is not very friendly even for experienced developers and I have the rule that the simpler tests the more complicated. 

Ideally tests are simple but mocking with string method names and arrays for args massively increases the cognitive load and makes things
harder to do test first easily. From my monkey handed experience there is a much greater chance of having to fix the mock code after
the implementation code has been written meaning the implementation code is now being used as the source of truth. Really there is
always a bidirectional relationship between code and test, the bias of being the truth should be heavily in the test.

[https://github.com/pbyrne84/phpmockito](https://github.com/pbyrne84/phpmockito)

There was a similar project but that used serialize which fatalled for certain things.

For example this will hard error as SplFileInfo is not serializable :-
```php
echo serialize(new SplFileInfo("moo.txt"));
```

### Why I stopped supporting and why it fundamentally became unsupportable.

1. The jvm was going to go through its third scriptengine change. There was rhino (https://github.com/mozilla/rhino) then 
   Nashorn (https://www.oracle.com/technical-resources/articles/java/jf14-nashorn.html) and that will move to graalvm
   (https://docs.oracle.com/en/graalvm/enterprise/20/docs/reference-manual/js/NashornMigrationGuide/). There was no documentation
   for this at the time. I believe currently the scripting stuff is the only thing PHPStorm now cannot do natively meaning most of this
   project is redundant.
2. The internals of PHPStorm kept changing to accommodate support for the more magical methods of PHP. Libraries used __call a lot to
   get around limitations of the language and to make PHPStorm play nice with what was created the internals had to fluctuate.
   Internal changes meant random edge cases would break. There is this project  https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment which has
   every edge case that so that when you rab inspections it should show no invalid method calls. When something was broken 
   then I had to use the https://plugins.jetbrains.com/plugin/227-psiviewer plugin to try and work out how the signature was garbled. 
   That is a not fun puzzle, more functionality, more edge cases more things broken. Not a case of if, but when.
3. I stopped doing PHP, so I would never notice if it was broken in some way.

#### Example internal signature

This is fairly simple one of 2 generic methods, **mock** and **when**. The result of **mock** is being passed to **when** and then **__call**
is being called on the returned *mock*. This signature is then used to give you autocomplete on the **__call** params.

```
signatureParts	[#M#π(#g#F\PHPMockito\EndToEnd\when)((#M#π(#g#F\PHPMockito\EndToEnd\mock)(\PHPMockito\TestClass\MagicMethodTestClass).__call)(#M#g#F\PHPMockito\EndToEnd\mock.__call)).thenReturn, #M#g#F\PHPMockito\EndToEnd\when.thenReturn]```
```

Do I know what it should look like, NO :) 
Each call was a case of trial and error and why there is little unit testing and mostly end-to-end testing using inspections.

Code for above signature:

```php
  $magicMethodTestClass = mock( MagicMethodTestClass::class );
  $fullyActionedMethodCall =
          when( $magicMethodTestClass->__call( 'magicMethodCall', array( 'testValue' ) ) )
                  ->thenReturn( $magicMethodCallResult );
```

### Timeline for demise.

The plugin currently uses **PHPTypeProvider3** which is deprecated, though **PhpTypeProvider2** still exists. When intellij upgrades
its default JVM then the scripting engine stuff will just likely just fail with exceptions.

### Thanks
Thanks to everyone who used and liked this project, it is nice to be appreciated :)

## Overview

Intellij/Phpstorm PHP plugin to allow a dynamic return type from method/function calls. It can use either the instance type of the passed parameter or a string lookup.
Note: Use fully qualified names.

Example project
https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment



## Installing from zip in deploy
download zip from 
https://github.com/pbyrne84/DynamicReturnTypePlugin/blob/master/deploy/DynamicReturnTypePlugin.zip

Delete original DynamicReturnTypePlugin folder in plugin directory and unzip new one in same location.

## Example usage, if done right no errors will be evident and things like methods such appendChild are automated refactor safe across tests.

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
        }
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

## 1.Masks
These were the original replacement strategy but it has been found to be a rather limiting approach to the problem of frameworks containers expecting strings as parameters that do not easily match the final result without some string reduction/manipulation.

It simply works by using String.format on the the result.

For example the following configuration.
```json
{
"class": "\\Phockito",
"method": "maskExample",
"position": 0,
"mask": "Test%sModel"
}
```

Would cause the following to return TestUserModel
```php
Phockito::maskExample('User');
```

There was no easy way to solve changing 'Entity\User' into 'MyNameSpace\User' without going down the path of writing a strategy for each custom user/framework request due to designs I have no knowledge of so cannot preempt. I would view this method as deprecated due to the limitations. But it will not be removed.

### 2.Script engine calls

### NOTE: Script engine jar paths can now be set using IDEA_GROOVY_JAR_PATH and IDEA_JAVASCRIPT_JAR_PATH environment variables. IDEA_JAVASCRIPT_JAR_PATH is for the path to the nashorn.jar file for javascript in JDK8.

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

returnTypeNameSpace and returnTypeClass are separated beforehand to ease manipulation within the function call. The returnTypeNameSpace trims the leading slash so \DomDocument is DomDocument.

### Replacement callback file handling/compilation.
The only restriction is the script file must be in the same folder as its related dynamicReturnTypeMeta.json. This restriction may be lifted at some point but it adds complexity.

Recompilation is triggered when a dynamicReturnTypeMeta.json is altered (say adding a space and pressing enter). This restriction will be removed now I can write compilation errors to the event log versus file log( basically compile on save will be offered).


### Api
A variable call api is injected into the script which allows communication back to the ide. This can be expanded on request.
A javascript file that will offer completion can be found here :
https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment/blob/master/ExecutingScriptApi.js

Example initialisation can be seen here at the top :
https://github.com/pbyrne84/DynamicReturnTypePluginTestEnvironment/blob/master/JavaScriptReplacementCallback.js


### Debugging
```js
api.writeToEventLog("your message")
````

Will write a message to the event log in the ide. Errors in your script will also appear here.

 