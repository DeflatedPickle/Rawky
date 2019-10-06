grammar RexPaint_Palette;

@header {
    package com.deflatedpickle.rawky.rexpaint_palette;
}

start: (row NEWLINE)+;

row: (hex | rgb)+;

hex: '#' code=(INT | CHAR)+;
rgb: '{' red=INT ',' green=INT ',' blue=INT  '}';

NEWLINE: '\r\n' | '\n';
INT: '0'..'9'+;
CHAR: 'A'..'Z' | 'a'..'z';

WS: [ \t] -> channel(HIDDEN);