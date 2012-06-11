~~~~~~~~~
anamyéter
~~~~~~~~~

A Python word and name generator useful in creating characters and foreign
languages for stories or games.

Background
==========

The Name
--------

Quasi-originally called PyWordGen (I forget the very first name), the project
has been renamed to something more interesting and far less easy to remember:
*anamyéter*. This name is really more a case of playfulness with
Proto-Indo-European and Proto-Celtic (with a touch of decidedly non-poetic
license) than an actual word. Here's the story:

The reconstructed word for "names" in PIE is *\*h₁néh₃mō*. One of the patterns
in PIE for making nouns into verbs (denominative) is the addition of an
ablauting thematic suffix: *\*-yé/ó-*. Finally, PIE has as agentive suffixes
*\*-t(e)r-* and *\*-t(o)r-* (hysterokinetic and amphikinetic. respectively). So
what does all this mean? Namely, this:

* *\*h₁néh₃mō* - names (nominative)

* *\*h₁néh₃mōyó* - to give names

* *\*h₁néh₃mōyótor* - on who gives names

But can you imagine that last one for a project name? Nah, I couldn't either.
So, I skipped a couple thousand years, and looked at Proto-Celtic instead:


* *anman* - name (nominative singular)

* *anmanyé* - to name

* *anmanyéter* - one who names

That last one was nice, but still a little awkward to pronounce. So I just
messed with it a bit, and we have "anamyéter" :-)

Project History
---------------

Once upon a time in the 90s, I was hacking some Perl with friends: we were
playing with cryptography and examining letter frequencies in different
languages at different historical times. Our curiosity (I dare not say
"hypothesis") had us wondering if character frequencies could indicate source
language in old encryption styles.

A short time later, I had the idea of using this code to assist users in a site
I maintained. I ran middlearth.net with as a PHPNuke site (hey, we all have
dark secrets; I'm just being honest about mine) and to assist with users who
wanted to create a Tolkien-like name for themselves, I wrote a PHP script that
generated names for any of the given races.

Later, I migrated the site to a Zope and then Plone CMS, converting the script
to Python. And finally, in 2003, I wanted to just be able to use the tool as a
command-line utility, whence this library.

Usage
=====
TBD
