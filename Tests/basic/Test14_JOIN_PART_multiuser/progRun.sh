#!/bin/bash
PAUSE=0.1
PORT=$((12000 + ($RANDOM % 1000)))  # avoids conflicts with other students
(timeout 4 java IrcServerMain irc.example.com $PORT > /dev/null 2>&1 ) & (sleep 1; bash $TESTDIR/sendMessages.sh | nc localhost $PORT 2>&1) & (sleep 1; bash $TESTDIR/sendMessages2.sh | nc localhost $PORT > /dev/null 2>&1)
wait
