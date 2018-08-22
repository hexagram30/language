# hexagram30/language

*A language and word generator for use in hexagram30 narratives*

[![][logo]][logo-large]


## Usage

From the command line:

```
$ lein fictional mythgarthur

 __                                                 ______ ______
|  |--.-----.--.--.---.-.-----.----.---.-.--------.|__    |      |
|     |  -__|_   _|  _  |  _  |   _|  _  |        ||__    |  --  |
|__|__|_____|__.__|___._|___  |__| |___._|__|__|__||______|______|

..................................  l a n g u a g e  .............



    Orcish: Khikuteeqareem bys dundewlulbuyoothind. Tachellunat ör.
            Wannadden beqtusodin we etsinklithu beqatet oollavoominokwaalle
            sleshranind aaldtorings.

    Elvish: Har spum. Mé vin ýð il. Þafe þeg vemattuð nadogaflimyga.

    Human: Shrimmornancafafetyaða stete breandensting eckeccusmaastelēamætipi
           sanh ǣhasior. Chaki sunse hwīktra dustravastin verigearn stōmh
           braprakanaff satundishagarapi. Late chamatyaunac fytu pittindaho.
           Hvelse elcumaathadhyan
           jargafunnacateceraremisharieftilastirandinammidvinampler oftectioble
           paapashetmand bíly þrónbhoneculy mririndisuas.

    Dwarvish: Mshg aca a withexynt unce. Lyrc fath lbn pòn dhèabhammilba tìr
              oblarboyn. Nbyaylie dienn stogabar ylk a vyth. A waplinaidh tha hvar.
              Marirama' aspainatem thannt sosvrysyp adachtnipylzie moch
              doireaselpyme gur yochtigund chui.
```
```
$ lein fictional rook

 __                                                 ______ ______
|  |--.-----.--.--.---.-.-----.----.---.-.--------.|__    |      |
|     |  -__|_   _|  _  |  _  |   _|  _  |        ||__    |  --  |
|__|__|_____|__.__|___._|___  |__| |___._|__|__|__||______|______|

..................................  l a n g u a g e  .............



    Rookish: Shisovey ruumoltirbusawaki ul dedo mucomaku jewn clogikahubleng
             der vit watikuntara. Cushhnomatsutamas ah irpartontioundiasta
             steashinamo. Tos. Cosbatagi thoshhnumohaxpoka desa cossebud
             wationdiri rer sustrayatocotably nuevibuctine. Loshirgasir ostistrar.
             Rudipnura ate hal bhuremahaa proyuckeponfoji esta hamastari
             denaanactimos detibut o. Chekunares quesephermaa pirnaghoum
             rejikambis dinegin eto pute shikabhar. Saipongekabi mampandun. Bhiip
             yukicioris asogeffento augatreaw karque quexpresdare. Unidackitae
             vatetioxtrerd guegigreeatinatuz ista hiff juededossetaugis.

    Elani: Lopiblitre u faung cehrellevre aaalemavraive mount batiorduessin.

    Jas: Nerhacakon za na. Ci juer eet a shuim. Kao uja ni. Hua zape choun trit
         ago beng zhe cheng. Shou ago xiang haing purs eemheing chou.

    Mux: Cezzle miss ung claki mudekichar pomashiro bezz. Po booki chtontottenk
         da marirr mange bass mmmmkorvara.
```

From the REPL:

```
    (require '[hxgm30.language.syntagmata.lang.core :as lang]
             '[hxgm30.language.syntagmata.lang.fictional.rook :as rook])
    (lang/paragraph rook/mux)
```
```
    Whizz ji reira nou pebodana suran yabisebt. Dan
    aravan curassouronan mume potikertarp gun kerr
    aruppitten dep bem. Lu whoom ido ong sluckoo. Lue
    oya nyaazz hesh kedirayo. Pum settewaxth lias.
    Phes gegigupe whistramew losomepe irstendeng
    gesiweck wegetiop boo bur.
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
Copyright © 2018, Hexagram30 <hexagram30@cnbb.games>
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
