#!/usr/bin/env python

import ez_setup
ez_setup.use_setuptools()
from setuptools import setup

setup(name="PyWordGen",
    version="1.2",
    description="Python Word Generator",
    author="Duncan McGreggor",
    author_email="duncan@adytums.us",
    url="http://pywordgen.sourceforge.net",
    packages=[
        'wordgen',
        'wordgen.corpora',
        'wordgen.languages',
    ],
    data_files=[('wordgen/corpora',
        ['wordgen/corpora/afrikaner_wordlist.txt',
        'wordgen/corpora/arabic_wordlist.txt',
        'wordgen/corpora/chinese_wordlist.txt',
        'wordgen/corpora/english_wordlist.txt',
        'wordgen/corpora/french_wordlist.txt',
        'wordgen/corpora/german_wordlist.txt',
        'wordgen/corpora/hindi_wordlist.txt',
        'wordgen/corpora/japanese_wordlist.txt',
        'wordgen/corpora/korean_wordlist.txt',
        'wordgen/corpora/russian_wordlist.txt',
        'wordgen/corpora/spanish_wordlist.txt']
    )],
)
