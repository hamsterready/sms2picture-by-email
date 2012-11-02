sms2picture-by-email
====================

Simple SMS listener which on SMS takes picture and sends it back via email.

How it works?
-------------
User provides two values: MSISDN (mobile number - do not forget about country code) and email address.

When SMS from provided MSISDN is received then:
 1. application will capture picture using back facing camera 
 2. upload it to http://imgur.com (public server)
 3. send email configured address with link to uploaded picture (sending email is via http://jangomail.com)

API selection (imgur and jangomail) where kind of random - whichever I found first via google ;)

User Interface
--------------
![Screenshot](https://raw.github.com/hamsterready/sms2picture-by-email/master/github/screenshot.png)


License
-------

Copyright (C) 2012 Maciej Lopacinski

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
