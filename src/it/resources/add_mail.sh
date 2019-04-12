#!/usr/bin/env bash
curl --url 'smtp://localhost:587'  --mail-from 'homer@simpson.cartoon' --mail-rcpt 'bart@simpson.cartoon' --upload-file /message.eml --user 'homer@simpson.cartoon:Mmm... donuts' --insecure
