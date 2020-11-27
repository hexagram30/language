# hexagram30/language

*A syntagmata and Markov chain language, word, and name generator for use in hexagram30 narratives*

[![][logo]][logo-large]


Note: This is an WIP port from the [Clojure version of the project](../../tree/clojure).
Only some of the functionality has been created here.

## Usage


### Command Line

Generate a name:

```
$ rebar3 name halfling male
```
```

```

Get supported races:

```
$ rebar3 name races
```

Get supported name types for a race:

```
$ rebar3 name types dwarf
```

Update the Markov chain stats files for the name generator:

```
$ rebar3 name regen
```

### REPL



```lisp

```


## Donating

A donation account for supporting development on this project has been set up
on Liberapay here:

* [https://liberapay.com/hexagram30/donate](https://liberapay.com/hexagram30/donate)

You can learn more about Liberapay on its [Wikipedia entry][libera-wiki] or on the
service's ["About" page][libera-about].

[libera-wiki]: https://en.wikipedia.org/wiki/Liberapay
[libera-about]: https://liberapay.com/about/


## License

```
Copyright © 2018-2019, Hexagram30 <hexagram30@cnbb.games>
Copyright © 2003-2012, Duncan McGreggor
Copyright © 2000-2002, Middleearth.net
Copyright © 1999, Duncan McGreggor

Apache License, Version 2.0
```

<!-- Named page links below: /-->

[logo]: https://raw.githubusercontent.com/hexagram30/resources/master/branding/logo/h30-logo-2-long-with-text-x695.png
[logo-large]: https://raw.githubusercontent.com/hexagram30/resources/master/branding/logo/h30-logo-2-long-with-text-x3440.png
[map]: https://raw.githubusercontent.com/hexagram30/map/master/resources/planets/001-mercator-altitude-small.jpg
[map-large]: https://raw.githubusercontent.com/hexagram30/map/master/resources/planets/001-mercator-altitude.jpg
