#!/bin/bash
# Executed concurrently with sendMessages.sh and sendMessages2.sh
sleep 0.3
echo "NICK Humpy"
echo "USER ha 0 * :Humphrey Appleby"
echo "JOIN #alas"
sleep 1.2
echo "PRIVMSG #alas :Yes prime minister."
sleep 0.6
echo "QUIT"
sleep 0.1