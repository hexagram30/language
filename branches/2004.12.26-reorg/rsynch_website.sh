#!/bin/bash
CHECK=$1
SOURCE_DIR="sourceforge_website/"
REMOTE_USER="oubiwann"
REMOTE_HOST="shell.sf.net"
REMOTE_DIR="pywordgen/"
RSYNC_BIN="rsync"
DEFAULT_OPTIONS="--exclude .svn* --recursive --stats  --progress --checksum"
if [ "$CHECK" = "check" ]; then
    echo
    echo "Hit ENTER to continue..."
    read
    echo "Performing a dry-run..."
    echo
   $RSYNC_BIN $DEFAULT_OPTIONS \
    --dry-run \
    $SOURCE_DIR $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR
elif [ "$CHECK" = "download" ]; then
    echo
    echo "To download files from production, hit ENTER."
    echo "To quit now, hit ^C (Control-C)..."
    echo
    read
    echo "Downloading files from production..."
    echo
    $RSYNC_BIN $DEFAULT_OPTIONS \
    $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR $SOURCE_DIR
else
    echo
    echo "To move files into production, hit ENTER."
    echo "To quit now, hit ^C (Control-C)..."
    echo
    read
    echo "Moving files into production..."
    echo
    $RSYNC_BIN $DEFAULT_OPTIONS \
    $SOURCE_DIR $REMOTE_USER@$REMOTE_HOST:$REMOTE_DIR
fi

