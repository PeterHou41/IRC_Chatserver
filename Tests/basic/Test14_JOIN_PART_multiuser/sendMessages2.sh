#!/bin/bash
# Executed concurrently with sendMessages.sh
sleep 0.3
echo "NICK Bob"
echo "USER b12 0 * :Robert Bobson"
echo "JOIN #guitars"
echo "PRIVMSG #guitars :Hi guys"
sleep 0.6
echo "PRIVMSG #guitars :Did you leave, Alice?"
sleep 0.6
echo "QUIT"
sleep 0.1
