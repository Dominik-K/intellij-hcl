package org.intellij.plugins.hcl.terraform.il.psi;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static org.intellij.plugins.hcl.terraform.il.TILElementTypes.*;

%%

%{
  public _TILLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _TILLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL="\r"|"\n"|"\r\n"
LINE_WS=[\ \t\f]
WHITE_SPACE=({LINE_WS}|{EOL})+

DOUBLE_QUOTED_STRING=\"([^\\\"\r\n]|\\[^\r\n])*\"?
NUMBER=-?(0x)?(0|[1-9])[0-9]*(\.[0-9]+)?([eE][-+]?[0-9]+)?
ID=[a-zA-Z\-_*][0-9a-zA-Z\-_*]*

%%
<YYINITIAL> {
  {WHITE_SPACE}               { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "${"                        { return INTERPOLATION_START; }
  "{"                         { return L_CURLY; }
  "}"                         { return R_CURLY; }
  "("                         { return L_PAREN; }
  ")"                         { return R_PAREN; }
  ","                         { return COMMA; }
  "="                         { return EQUALS; }
  "."                         { return POINT; }
  "+"                         { return OP_PLUS; }
  "-"                         { return OP_MINUS; }
  "*"                         { return OP_MUL; }
  "/"                         { return OP_DIV; }
  "%"                         { return OP_MOD; }
  "true"                      { return TRUE; }
  "false"                     { return FALSE; }
  "null"                      { return NULL; }

  {DOUBLE_QUOTED_STRING}      { return DOUBLE_QUOTED_STRING; }
  {NUMBER}                    { return NUMBER; }
  {ID}                        { return ID; }

  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}
