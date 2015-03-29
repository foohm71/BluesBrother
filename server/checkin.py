#!/usr/bin/python

import sys
import re
import os
import getopt
from datetime import datetime
from datetime import timedelta
import time

sendmail_script = "./sendmail.py"
to_email = "devicemanager\@yahoo-inc.com"
checkin = {}
seconds_in_hour = 60 * 60

def readLogFile(infile):
   f = open(infile, "r")
   for line in f:
      time,deviceID = line.split(" : ")
      # print deviceID + " - " + time
      d = deviceID.rstrip('\n')
      checkin[d] = time
   f.close()

def checkLastCheckin():
   deviceList = []
   now = datetime.now()
   for k in checkin.keys():
      # print k + " - " + checkin[k]
      date = datetime.strptime(checkin[k], "%a %b %d %H:%M:%S %Y")
      tdelta = now - date
      if (tdelta.total_seconds() > seconds_in_hour):
         deviceList.append(k)
   return deviceList

def sendmail(devicelist):
   now = (datetime.now()).strftime("%a %b %d %H:%M:%S %Y")
   subj = "Device Library alert for " + now
   msg = "The list of devices not checked in for 1 hr are:\n"
   for d in devicelist:
      msg += d + "\n"
   os.system(sendmail_script + " -t " + to_email + " -s \'" + subj + "\' -m \'" + msg + "\'")

def main():
   while True:
      readLogFile("./blues-server.log")
      devicelist = checkLastCheckin()
      if len(devicelist) > 0:
         sendmail(devicelist)
      time.sleep(5 * 60)

if __name__ == "__main__":
    main()
