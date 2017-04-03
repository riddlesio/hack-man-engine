#!/bin/bash

BASEDIR=`pwd`
java -jar $BASEDIR/game-wrapper.jar "$(cat wrapper-commands.json)"
