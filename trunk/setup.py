#!/usr/bin/env python

from distutils.core import setup

setup(name="PyWordGen",
    version="1.0",
    description="Python Word Generator",
    author="Duncan McGreggor",
    author_email="duncan@adytumsolutions.com",
    url="http://pywordgen.sf.net",
    packages=[
        'adytum',
        'adytum.linguistics',
        'adytum.linguistics.corpora',
        'adytum.linguistics.syntagma',
    ],
    data_files=[('adytum/linguistics/corpora',
        ['lib/linguistics/corpora/afrikaner_wordlist.txt',
        'lib/linguistics/corpora/arabic_wordlist.txt',
        'lib/linguistics/corpora/chinese_wordlist.txt',
        'lib/linguistics/corpora/english_wordlist.txt',
        'lib/linguistics/corpora/french_wordlist.txt',
        'lib/linguistics/corpora/german_wordlist.txt',
        'lib/linguistics/corpora/hindi_wordlist.txt',
        'lib/linguistics/corpora/japanese_wordlist.txt',
        'lib/linguistics/corpora/korean_wordlist.txt',
        'lib/linguistics/corpora/russian_wordlist.txt',
        'lib/linguistics/corpora/spanish_wordlist.txt']
    )],
    package_dir = {'adytum': 'lib'},
)
