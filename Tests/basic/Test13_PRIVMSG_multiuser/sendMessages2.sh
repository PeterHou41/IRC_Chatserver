#!/bin/bash
# Executed concurrently with sendMessages.sh
sleep 0.3
echo "NICK Bob"
echo "USER b12 0 * :Robert Bobson"
echo "PRIVMSG Alice :Hello Alice!"
echo "QUIT"
sleep 0.1
