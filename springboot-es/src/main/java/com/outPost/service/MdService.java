package com.outPost.service;

import com.jiahao.markdown.*;
import com.jiahao.markdown.builder.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class MdService {


    public List<Block> analyze(String content) {
        List<TextOrTable> list = tableFilter(content);
        List<Block> blocks = new ArrayList<Block>();
        for (TextOrTable textAndTable : list) {
            if (textAndTable.isTable()) {
                Block block = new Block();
                block.setType(BlockType.TABLE);
                block.setTableData(textAndTable.getTableData());
                blocks.add(block);
            } else {
                List<Block> tmps = analyzeTextNoTable(textAndTable.getReader());
                for (Block block : tmps) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    /**
     * 不包含table格式的语法解析
     *
     * @param reader
     * @return
     */
    private static List<Block> analyzeTextNoTable(BufferedReader reader) {
        List<Block> list = new ArrayList<Block>();
        try {
            List<String> lines = new ArrayList<String>();
            String tmp = reader.readLine();
            while (tmp != null) {    //将内容每一行都存入list中
                lines.add(tmp);
                tmp = reader.readLine();
            }
            boolean hasCode = true;    //内容是否包含代码格式的标志
            for (int idx = 0, si = lines.size(); idx < si; idx++) {
                Block block = null;
                String str = lines.get(idx);
                if (str.trim().equals("")) {    //空行直接忽略
                    continue;
                }
                if (str.trim().startsWith(MDToken.CODE) && hasCode) {
                    StringBuilder sb = new StringBuilder();
                    boolean isCodeEnd = false;
                    for (int idx1 = (idx + 1); idx1 < si; idx1++) {
                        str = lines.get(idx1);
                        if (str.trim().equals(MDToken.CODE)) {    //检查是否有代码结束符
                            isCodeEnd = true;
                            idx = idx1;
                            break;
                        } else {
                            sb.append(str + "\n");
                        }
                    }

                    if (isCodeEnd) {
                        block = new CodeBuilder(sb.toString()).bulid();
                    } else {    //没代码结束符，则下次不会再进来检查代码格式，游标置回代码格式检查之前
                        idx = idx - 1;
                        hasCode = false;
                        continue;
                    }
                } else if (str.startsWith(MDToken.CODE_BLANK)) {
                    Object[] tmps = analyzerList(idx, lines, new ListBuilderCon() {
                        @Override
                        public Block newBuilder(String str) {
                            return new CodeBuilder(str).bulid();
                        }

                        @Override
                        public boolean isRightType(String lineStr) {
                            return lineStr.startsWith(MDToken.CODE_BLANK);
                        }

                        @Override
                        public StringBuilder how2AppendIfBlank(StringBuilder sb) {
                            return sb.append("\n");
                        }

                        public StringBuilder how2AppendIfNotBlank(StringBuilder sb, String value) {
                            return sb.append(value.substring(MDToken.CODE_BLANK.length()) + "\n");
                        }
                    });

                    idx = (Integer) tmps[0];
                    block = (Block) tmps[1];
                } else if (str.trim().startsWith(MDToken.HEADLINE)) {
                    block = new HeaderBuilder(str).bulid();
                } else if (isQuote(str)) {
                    Object[] tmps = analyzerList(idx, lines, new ListBuilderCon() {
                        public Block newBuilder(String str) {
                            return new QuoteBuilder(str).bulid();
                        }

                        public boolean isRightType(String lineStr) {
                            return isQuote(lineStr);
                        }
                    });
                    idx = (Integer) tmps[0];
                    block = (Block) tmps[1];
                } else if (isUnOrderedList(str)) {
                    Object[] tmps = analyzerList(idx, lines, new ListBuilderCon() {
                        public Block newBuilder(String str) {
                            return new UnorderedListBuilder(str).bulid();
                        }

                        public boolean isRightType(String lineStr) {
                            return isUnOrderedList(lineStr);
                        }
                    });

                    idx = (Integer) tmps[0];
                    block = (Block) tmps[1];
                } else if (isOrderedList(str)) {
                    Object[] tmps = analyzerList(idx, lines, new ListBuilderCon() {
                        public Block newBuilder(String str) {
                            return new OrderedListBuilder(str).bulid();
                        }

                        public boolean isRightType(String lineStr) {
                            return isOrderedList(lineStr);
                        }
                    });

                    idx = (Integer) tmps[0];
                    block = (Block) tmps[1];
                } else {
                    if ((idx + 1) < si) {
                        String nextStr = lines.get(idx + 1);
                        int lvl = HeaderBuilder.isRightType(nextStr);
                        if (lvl > 0) {
                            block = new HeaderBuilder(str).bulid(lvl);
                            idx++;
                        }
                    }
                    if (block == null) {
                        block = new CommonTextBuilder(str).bulid();
                    }
                }
                if (block != null) {
                    list.add(block);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return list;
    }

    private List<TextOrTable> tableFilter(String content) {
        List<TextOrTable> list = new ArrayList<TextOrTable>();
        List<String> lines = new ArrayList<String>();

        try {
            String[] split = content.split("\n");
            System.out.println("元素个数"+split.length);
            for (String tmp : split) {    //将内容每一行都存入list中
                lines.add(tmp);
            }
//			boolean inCode = false;
            StringBuffer sb = new StringBuffer();
            for (int i = 0, l = lines.size(); i < l; i++) {
                String str = lines.get(i);
                boolean hasTable = false;
                if (str.indexOf("|") > -1) {    //检查是否有table的分隔符
                    hasTable = true;
                    boolean isStart = false;
                    boolean isEnd = false;
                    if (str.startsWith("\\|")) {    //去头
                        str = str.substring(1);
                        isStart = true;
                    }
                    if (str.endsWith("\\|")) {    //去尾
                        str = str.substring(0, str.length() - 1);
                        isEnd = true;
                    }
                    String[] parts = str.split("\\|");
                    if (parts.length <= 1 && !(isStart && isEnd)) {
                        hasTable = false;
                    }
                }
                if (hasTable) {
                    if ((i + 1) < l) {    //检查到符合规范的table头之后，检测下一行是否为 ---|---的类似字符串
                        String nextLine = lines.get(i + 1);
                        String[] nextParts = nextLine.split("\\|");
                        for (String part : nextParts) {

                            part = part.trim().replaceAll("-", "");
                            if (part.length() > 0) {
                                hasTable = false;
                            }
                            if (!hasTable) {
                                break;
                            }
                        }
                    } else {
                        hasTable = false;
                    }
                }
                if (hasTable) {    //检查到真的有table存在

                    if (!sb.toString().equals("")) {    //把已存入stringbuffer的内容先归档
                        TextOrTable text = new TextOrTable(false);
                        text.setReader(new BufferedReader(new StringReader(sb.toString())));
                        list.add(text);

                        sb = new StringBuffer("");    //将stringbuffer重新置为空
                    }

                    List<List<String>> tableDataList = new ArrayList<List<String>>();
                    int tableLineNum = i + 1;    //---|---的行数，此行不能放入table的data
                    for (int j = i; j < l; j++) {
                        if (j == tableLineNum) {
                            continue;
                        }
                        String tableLine = lines.get(j);
                        String[] cellDatas = tableLine.split("\\|");
                        if (cellDatas.length >= 2) {    //此行是table的数据
                            tableDataList.add(Arrays.asList(cellDatas));
                            if (j == (l - 1)) {    //到内容底部，table数据结束，归档
                                tableDataList = trimTableData(tableDataList);
                                TextOrTable table = new TextOrTable(true);
                                table.setTableData(tableDataList);
                                list.add(table);

                                i = j;    //设置游标，跳出循环
                                break;
                            }
                        } else {    //table数据结束，归档
                            tableDataList = trimTableData(tableDataList);
                            TextOrTable table = new TextOrTable(true);
                            table.setTableData(tableDataList);
                            list.add(table);

                            i = (j - 1);    //设置游标，跳出循环
                            break;
                        }
                    }
                } else {
                    sb.append(str + "\n");
                }
            }
            if (!sb.toString().equals("")) {
                TextOrTable text = new TextOrTable(false);
                text.setReader(new BufferedReader(new StringReader(sb.toString())));
                list.add(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 删除表格数据中的头尾空白列
     *
     * @param tableDataList
     * @return
     */
    private static List<List<String>> trimTableData(List<List<String>> tableDataList) {
        boolean isFirstEmpty = true;
        boolean isLastEmpty = true;
        for (int k = 0, m = tableDataList.size(); k < m; k++) {
            List<String> tmps = tableDataList.get(k);
            if (!tmps.get(0).trim().equals("") && isFirstEmpty) {
                isFirstEmpty = false;
            }
            if (!tmps.get(tmps.size() - 1).trim().equals("") && isLastEmpty) {
                isLastEmpty = false;
            }
        }
        if (isLastEmpty) {
            for (int k = 0, m = tableDataList.size(); k < m; k++) {
                List<String> tmps = tableDataList.get(k);
                List<String> newTmps = new ArrayList<String>();
                for (int n = 0, o = tmps.size(); n < o; n++) {
                    if (n < (o - 1)) {
                        newTmps.add(tmps.get(n));
                    }
                }
                tableDataList.set(k, newTmps);
            }
        }
        if (isFirstEmpty) {
            for (int k = 0, m = tableDataList.size(); k < m; k++) {
                List<String> tmps = tableDataList.get(k);
                List<String> newTmps = new ArrayList<String>();
                for (int n = 0, o = tmps.size(); n < o; n++) {
                    if (n > 0) {
                        newTmps.add(tmps.get(n));
                    }
                }
                tableDataList.set(k, newTmps);
            }
        }
        return tableDataList;
    }

    private static Object[] analyzerList(int idx, List<String> lines, ListBuilderCon listBuilderCon) {
        StringBuilder sb = new StringBuilder();
        int si = lines.size();
        for (int idx1 = idx; idx1 < si; idx1++) {
            String str = lines.get(idx1);
            if (str.trim().equals("")) {
                idx = idx1;
                sb = listBuilderCon.how2AppendIfBlank(sb);
                continue;
            }
            if (!listBuilderCon.isRightType(str)) {    //检查是否为列表语法
                idx = idx1 - 1;
                break;
            } else {
                sb = listBuilderCon.how2AppendIfNotBlank(sb, str);
            }
            if (idx1 == (si - 1)) {    //是否已到行尾
                idx = idx1;
            }
        }
        Object[] tmps = new Object[2];
        tmps[0] = idx;
        tmps[1] = listBuilderCon.newBuilder(sb.toString());
        return tmps;
    }

    private static boolean isQuote(String str) {
        return str.trim().startsWith(MDToken.QUOTE);
    }

    private static boolean isUnOrderedList(String str) {
        return str.trim().startsWith(MDToken.UNORDERED_LIST1) || str.trim().startsWith(MDToken.UNORDERED_LIST2);
    }

    private static boolean isOrderedList(String str){
        return Pattern.matches("^[\\d]+\\. [\\d\\D][^\n]*$", str);
    }

}
