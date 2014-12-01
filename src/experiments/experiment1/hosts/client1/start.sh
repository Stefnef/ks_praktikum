#!/bin/bash

# Hier Pfad zu den Java-class-files eintragen
export PATH_TO_CLASSFILES="../../../../../out/production/KS_Praktikum"

groovy -cp $PATH_TO_CLASSFILES":../../../../../libs/jpcap.jar" -D stand.alone Client.groovy

