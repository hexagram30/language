svn diff ChangeLog | \
    egrep '^\+' | \
    sed -e 's/^\+//g'| \
    egrep -v '^\+\+ ChangeLog' > commit-msg
echo "Committing with this message:"
cat commit-msg
echo
echo "Committing to Subversion now..."
svn commit --file commit-msg && \
    rm commit-msg || \
    echo "There was an error committing; message preserved."

