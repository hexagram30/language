#!/bin/bash

CLONE_DIR=resources/wordnet
if [ ! -d $CLONE_DIR ]; then
    git clone --depth=1 https://github.com/clojusc/wordnet.git $CLONE_DIR
    cd resources/wordnet && \
        git submodule update --init data
fi
