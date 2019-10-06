grammar JASC_PAL;

@header {
    package com.deflatedpickle.rawky.jasc_pal;
}

start: header NEWLINE version NEWLINE colour_count (NEWLINE rgb)+;

header: 'JASC-PAL';
version: INT;
colour_count: INT;
rgb: INT SPACE INT SPACE INT;

SPACE: ' ';
NEWLINE: '\r\n' | '\n';
INT: [0-9]+;