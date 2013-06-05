DynamicReturnTypePlugin
=======================

Intellij/Phpstorm PHP plugin to allow a dynamic return type from method/function calls. It can use either the instance type of the passed parameter or a string lookup.

Uses
----
1. Generic like calls to simulate Phockito ( PHP Mockito) verify calls that return the object passed in.
eg. Phockito php example
```php
class DynamicReturnTypeTest extends PHPUnit_Framework_TestCase
{
    /** @var  Phockito */
    public $phockito;

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
Currently it looks for a file called dynamicReturnTypeMeta.json in the root of the project. An example json file for the above test is
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
