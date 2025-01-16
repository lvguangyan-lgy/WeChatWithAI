package com.rgzn.ttd.enums;

public enum PromptEnum {
    SQL_PROMPT("你是一个数据分析师,SQL大神,请根据用户提供的表的信息，以及用户的需求，写出一条满足用户需求的SQL, 条件字段使用模糊匹配.其中,chTableComment对应的值为表说明,chTableName对应的值为表名称,chFiled对应的值为字段名称,chComment对应的值为字段说明","并且要求输出的SQL以#开头,以#结尾,输出不需要其他说明和解释,输出样例如下：#SELECT field1 as 字段1,field2 as 字段2 FROM table#"),

    SQL_FILL_PROMPT("你是一个数据分析师,SQL大神,请根据模板问题、模板sql、表的信息和用户的需求，写出一条满足用户需求的SQL, 条件字段使用模糊匹配.其中,chTableComment对应的值为表说明,chTableName对应的值为表名称,chFiled对应的值为字段名称,chComment对应的值为字段说明","并且要求输出的SQL以#开头,以#结尾,输出不需要其他说明和解释,输出样例如下：#SELECT field1 as 字段1,field2 as 字段2 FROM table where column = 'value'#"),

    SQL_CORRECT_PROMPT("你是一个数据分析师,SQL大神,请结合报错信息和表信息去修正SQL.其中,chTableComment对应的值为表说明,chTableName对应的值为表名称,chFiled对应的值为字段名称,chComment对应的值为字段说明","并且要求输出修正后的SQL以#开头,以#结尾,输出不需要其他说明和解释,输出样例如下：#SELECT field1 as 字段1,field2 as 字段2 FROM table#"),

    TABLE_PROMPT("请根据用户的问题和业务表,匹配出相似度(从语义相关性、关键词重合度、文本表述差异、相关度评分维度进行相关度计算)大于0.6的业务表,匹配相似度在0-1区间.其中,chTableComment对应的值为表说明,chTableName对应的值为表名称","直接输出表名称，输出不需要其他说明和解释,以#开头,以#结尾,多个表名称使用符号\"|\"进行分隔，样例如下:#test_table1|test_table2#"),

    DESCRIBE_PROMPT("你是一个电力交易行业的专家,请结合用户问题、表信息和业务数据,对业务数据进行分析说明","要求输出一段详细的分析说明"),
    SIMPLE_DESCRIBE_PROMPT("你是一个电力交易行业的专家,请结合用户问题、表信息和业务数据,对业务数据进行分析说明","要求输出一段分析说明"),
    DATE_PROMPT("请判断用户的问题是否存在时间概念,如果不存在,则返回用户的问题;如果存在,则根据当前时间和用户的问题,把问题中的时间转换成标准的年月日时间","直接输出经过时间转换后的问题，输出不需要其他说明和解释,以#开头,以#结尾#"),
    //ECHARTS_PROMPT("请根据用户问题意图,分析用户是否希望返回图表,如果是则返回true,否则返回false","要求输出true或false,以#开头,以#结尾,输出不需要其他说明和解释,返回样例如下：#true#"),
    DATA_TYPE_PROMPT("请根据用户问题,分析用户期望获取的数据类型，数据类型包括:文本、图表、表格)，如果是文本则返回text,如果是图表则返回echarts,如果是表格则返回table","以#开头,以#结尾,输出不需要其他说明和解释,返回样例如下：#echarts#"),

    SIMILARITY_PROMPT("在模板列表中,返回跟用户问题相关度(从语义相关性、关键词重合度、文本表述差异、相关度评分维度进行相关度计算)最大的选项,相关度数值范围在0-1","并且要求输出模板列表中相关度最高的选项和相关度数值,以#开头,以#结尾,输出不需要其他说明和解释,样例如下：#今天天气真不错|0.8#");

    /**
     * 指令
     */
    private final String instruction;

    /**
     * 输出指引
     */
    private final String outputIndicator;

    PromptEnum(String instruction, String outputIndicator) {
        this.instruction = instruction;
        this.outputIndicator = outputIndicator;
    }

    public String getInstruction() {
        return instruction;
    }


    public String getOutputIndicator() {
        return outputIndicator;
    }

}
