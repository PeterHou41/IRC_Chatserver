#!/bin/bash
PAUSE=0.1
PORT=$((12000 + ($RANDOM % 1000)))  # avoids conflicts with other students
(timeout 2 java IrcServerMain irc.example.com $PORT > /dev/null 2>&1 ) & (sleep 1; bash $TESTDIR/sendMessages.sh | nc localhost $PORT 2>&1 | grep --color=none -oF -f $TESTDIR/expected.out)
wait
