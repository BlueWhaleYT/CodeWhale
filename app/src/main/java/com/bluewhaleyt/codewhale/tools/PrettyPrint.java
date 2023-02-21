package com.bluewhaleyt.codewhale.tools;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class PrettyPrint {

    /* Usage:
        - PrettyPrint.byBracket(str).toString();
        - PrettyPrint.byTags(str).toString();
    */

    public static PrinterBracket byBracket(String text) {
        final PrinterBracket bracket = (new PrettyPrint()).new PrinterBracket();
        bracket.load(text);
        return bracket;
    }

    public static PrinterTags byTags(String text) {
        final PrinterTags tags = (new PrettyPrint()).new PrinterTags();
        tags.load(true, "html");
        return tags;
    }

    public class Printer {
        private StringBuilder builder = new StringBuilder();
        public StringBuilder data() {
            return builder;
        }

        @NonNull
        @Override
        public String toString() {
            return builder.toString().trim();
        }

        public String indent(int count) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < count; i++) {
                result.append("\t");
            }
            return result.toString();
        }
    }

    public String first(String source, int length) {
        return source.substring(0, Math.min(source.length(), length));
    }

    public String alter(String source, int length) {
        return source.length() > length ? source.substring(length) : "";
    }

    public class PrinterBracket extends Printer {
        private BracketStatus state = new BracketStatus();
        public PrinterBracket() {
            super();
        }
        public void load(String text) {
            data().setLength(0);
            String target = "\n";
            text += target;
            state = new BracketStatus();
            while (text.contains(target)) {
                int index = text.indexOf(target);
                appendLine(text.substring(0, index));
                text = text.substring(1 + index);
            }
        }
        public void appendLine(String next) {
            next = next.trim();
            String thisLine = "";
            int brackets = 0;
            state.setString(false);
            int currentIndent = (!state.inComment() && next.startsWith("}")) ? state.brackets() -1 : state.brackets();
            String tempCommentIndent = "";

            while (next.length() > 0) {
                int skip = 1;
                boolean noComment = true;
                if (state.inString()) {
                    noComment = false;
                    if (next.startsWith("\\")) {
                        skip++;
                    }
                    state.setString(!next.startsWith("\""));
                } else {
                    if (state.inComment()) {
                        if (thisLine.length() == 0) {
                            next = next.trim();
                            if (next.startsWith("*")) {
                                tempCommentIndent = "";
                                while (next.startsWith("*")) {
                                    tempCommentIndent += first(next, 1);
                                    next = alter(next, 1);
                                }
                                if (!next.startsWith("/")) {
                                    tempCommentIndent += " ";
                                }
                            } else {
                                tempCommentIndent += " * ";
                            }
                        }
                        next = next.trim();
                        noComment = next.startsWith("*/");
                        if (noComment) {
                            state.setComment(false);
                            skip++;
                        }
                    } else if (next.startsWith("/*")) {
                        state.setComment(true);
                        skip++;
                        noComment = false;
                    } else if(next.startsWith("//")) {
                        skip = next.length();
                        noComment = false;
                    }
                    thisLine += first(next, skip);
                    next = alter(next, skip);
                    if (noComment) {
                        if (thisLine.endsWith("{")) {
                            state.addBracket();
                        } else if (thisLine.endsWith("}")) {
                            state.removeBracket();
                        }
                    }
                }
            }
            data().append((data().length() > 0 ? "\n" : "") + indent(currentIndent) + tempCommentIndent + thisLine);
        }
    }

    private class BracketStatus {
        public int indent;
        private boolean strOpen, commentOpen = false;
        void setString(boolean bool) {
            strOpen = bool;
        }
        boolean inString() {
            return strOpen;
        }
        void setComment(boolean bool) {
            commentOpen = bool;
        }
        boolean inComment() {
            return commentOpen;
        }
        int brackets() {
            return indent;
        }
        void addBracket() {
            indent++;
        }
        void removeBracket() {
            if (indent > 0) indent--;
        }
    }

    public class PrinterTags extends Printer {
        private TagStatus state = new TagStatus();
        private String mode = "xml";
        public PrinterTags() {
            super();
        }
        public void load(boolean expandAttr, String text) {
            int te = 0;
            te = tagEnd(text);
            while (te > 0) {
                te = tagEnd(text);
                String next = text.substring(0, te);
                text = text.substring(te + 1);
                appendTag(new Tag(next));
            }
        }
        public void appendTag(Tag next) {

        }
        private int tagEnd(String data) {
            int pos = -1;
            data = data.trim();
            if (data.startsWith("<")) {
                boolean inString = false;
                int p = 1;
                while (data.length() > 0) {
                    String next = data.substring(0, 1);
                    data = data.substring(1);
                    if (next.equals("\\") && data.length() > 0) data = data.substring(1);
                    if (!inString && next.equals(">")) {
                        pos = p;
                        break;
                    } else {
                        p++;
                    }
                    if (next.equals("\"")) inString = !inString;
                }
            } else {
                pos = data.indexOf("<");
            }
            return pos;
        }
    }

    private class Tag {
        public int open = 1;
        public int close = -1;
        public int text = 0;
        public int type = text;
        public Tag(String data) {}
    }

    private class TagStatus {
        private int indent;
        protected ArrayList<Tag> tagList = new ArrayList<>();
        public Tag last() {
            Tag r = null;
            if (tagList.size() > 0) r =tagList.get(tagList.size() - 1);
            return r;
        }
    }

}
