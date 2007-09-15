#!/usr/bin/env python

import ez_setup
ez_setup.use_setuptools()
from setuptools import setup

setup(name="PyWordGen",
    version="1.1",
    description="Python Word Generator",
    author="Duncan McGreggor",
    author_email="duncan@adytums.us",
    url="http://pywordgen.sourceforge.net",
    packages=[
        'adytum',
        'adytum.ling',
        'adytum.ling.corpora',
        'adytum.ling.syntagma',
    ],
    namespace_packages = ['adytum'],
    data_files=[('adytum/ling/corpora',
        ['adytum/ling/corpora/afrikaner_wordlist.txt',
        'adytum/ling/corpora/arabic_wordlist.txt',
        'adytum/ling/corpora/chinese_wordlist.txt',
        'adytum/ling/corpora/english_wordlist.txt',
        'adytum/ling/corpora/french_wordlist.txt',
        'adytum/ling/corpora/german_wordlist.txt',
        'adytum/ling/corpora/hindi_wordlist.txt',
        'adytum/ling/corpora/japanese_wordlist.txt',
        'adytum/ling/corpora/korean_wordlist.txt',
        'adytum/ling/corpora/russian_wordlist.txt',
        'adytum/ling/corpora/spanish_wordlist.txt']
    )],
)
