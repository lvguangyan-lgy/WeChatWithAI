package com.rgzn.ttd.model;

import javax.persistence.*;

@Table(name = "mk_td_his_elec")
public class MkTdHisElec {
    @Id
    @Column(name = "ch_id")
    private String chId;

    /**
     * 统一信用代码
     */
    @Column(name = "user_credit_code")
    private String userCreditCode;

    /**
     * 市场主体编码
     */
    @Column(name = "participant_code")
    private String participantCode;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 用户名称
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 用户类型(01：批发用户、02：零售用户)
     */
    @Column(name = "user_type")
    private String userType;

    /**
     * 用户户号
     */
    @Column(name = "user_code")
    private String userCode;

    /**
     * 所属交易中心编码（01: 广东, 06：海南）
     */
    @Column(name = "exchange_code")
    private String exchangeCode;

    /**
     * 计量点编号
     */
    @Column(name = "metering_code")
    private String meteringCode;

    /**
     * 计量点名称
     */
    @Column(name = "metering_name")
    private String meteringName;

    /**
     * 代理售电公司统一社会信用代码
     */
    @Column(name = "agency_credit_code")
    private String agencyCreditCode;

    /**
     * 代理售电公司id
     */
    @Column(name = "elec_agency_id")
    private String elecAgencyId;

    /**
     * 代理售电公司名称
     */
    @Column(name = "elec_agency_name")
    private String elecAgencyName;

    /**
     * 年月（yyyy-MM）
     */
    @Column(name = "ch_year_month")
    private String chYearMonth;

    /**
     * 月总历史用电量
     */
    @Column(name = "his_elec")
    private Long hisElec;

    /**
     * 月峰时段历史用电量
     */
    @Column(name = "peak_his_energy")
    private Long peakHisEnergy;

    /**
     * 月平时段历史用电量
     */
    @Column(name = "flat_his_energy")
    private Long flatHisEnergy;

    /**
     * 月谷时段历史用电量
     */
    @Column(name = "valley_his_energy")
    private Long valleyHisEnergy;

    /**
     * 月尖时段历史用电量
     */
    @Column(name = "spike_his_energy")
    private Long spikeHisEnergy;

    /**
     * @return ch_id
     */
    public String getChId() {
        return chId;
    }

    /**
     * @param chId
     */
    public void setChId(String chId) {
        this.chId = chId;
    }

    /**
     * 获取统一信用代码
     *
     * @return user_credit_code - 统一信用代码
     */
    public String getUserCreditCode() {
        return userCreditCode;
    }

    /**
     * 设置统一信用代码
     *
     * @param userCreditCode 统一信用代码
     */
    public void setUserCreditCode(String userCreditCode) {
        this.userCreditCode = userCreditCode;
    }

    /**
     * 获取市场主体编码
     *
     * @return participant_code - 市场主体编码
     */
    public String getParticipantCode() {
        return participantCode;
    }

    /**
     * 设置市场主体编码
     *
     * @param participantCode 市场主体编码
     */
    public void setParticipantCode(String participantCode) {
        this.participantCode = participantCode;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取用户名称
     *
     * @return user_name - 用户名称
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名称
     *
     * @param userName 用户名称
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取用户类型(01：批发用户、02：零售用户)
     *
     * @return user_type - 用户类型(01：批发用户、02：零售用户)
     */
    public String getUserType() {
        return userType;
    }

    /**
     * 设置用户类型(01：批发用户、02：零售用户)
     *
     * @param userType 用户类型(01：批发用户、02：零售用户)
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 获取用户户号
     *
     * @return user_code - 用户户号
     */
    public String getUserCode() {
        return userCode;
    }

    /**
     * 设置用户户号
     *
     * @param userCode 用户户号
     */
    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    /**
     * 获取所属交易中心编码（06：海南）
     *
     * @return exchange_code - 所属交易中心编码（06：海南）
     */
    public String getExchangeCode() {
        return exchangeCode;
    }

    /**
     * 设置所属交易中心编码（06：海南）
     *
     * @param exchangeCode 所属交易中心编码（06：海南）
     */
    public void setExchangeCode(String exchangeCode) {
        this.exchangeCode = exchangeCode;
    }

    /**
     * 获取计量点编号
     *
     * @return metering_code - 计量点编号
     */
    public String getMeteringCode() {
        return meteringCode;
    }

    /**
     * 设置计量点编号
     *
     * @param meteringCode 计量点编号
     */
    public void setMeteringCode(String meteringCode) {
        this.meteringCode = meteringCode;
    }

    /**
     * 获取计量点名称
     *
     * @return metering_name - 计量点名称
     */
    public String getMeteringName() {
        return meteringName;
    }

    /**
     * 设置计量点名称
     *
     * @param meteringName 计量点名称
     */
    public void setMeteringName(String meteringName) {
        this.meteringName = meteringName;
    }

    /**
     * 获取代理售电公司统一社会信用代码
     *
     * @return agency_credit_code - 代理售电公司统一社会信用代码
     */
    public String getAgencyCreditCode() {
        return agencyCreditCode;
    }

    /**
     * 设置代理售电公司统一社会信用代码
     *
     * @param agencyCreditCode 代理售电公司统一社会信用代码
     */
    public void setAgencyCreditCode(String agencyCreditCode) {
        this.agencyCreditCode = agencyCreditCode;
    }

    /**
     * 获取代理售电公司id
     *
     * @return elec_agency_id - 代理售电公司id
     */
    public String getElecAgencyId() {
        return elecAgencyId;
    }

    /**
     * 设置代理售电公司id
     *
     * @param elecAgencyId 代理售电公司id
     */
    public void setElecAgencyId(String elecAgencyId) {
        this.elecAgencyId = elecAgencyId;
    }

    /**
     * 获取代理售电公司名称
     *
     * @return elec_agency_name - 代理售电公司名称
     */
    public String getElecAgencyName() {
        return elecAgencyName;
    }

    /**
     * 设置代理售电公司名称
     *
     * @param elecAgencyName 代理售电公司名称
     */
    public void setElecAgencyName(String elecAgencyName) {
        this.elecAgencyName = elecAgencyName;
    }

    /**
     * 获取年月（yyyy-MM）
     *
     * @return year_month - 年月（yyyy-MM）
     */
    public String getChYearMonth() {
        return chYearMonth;
    }

    /**
     * 设置年月（yyyy-MM）
     *
     * @param chYearMonth 年月（yyyy-MM）
     */
    public void setYearMonth(String chYearMonth) {
        this.chYearMonth = chYearMonth;
    }

    /**
     * 获取月总历史用电量
     *
     * @return his_elec - 月总历史用电量
     */
    public Long getHisElec() {
        return hisElec;
    }

    /**
     * 设置月总历史用电量
     *
     * @param hisElec 月总历史用电量
     */
    public void setHisElec(Long hisElec) {
        this.hisElec = hisElec;
    }

    /**
     * 获取月峰时段历史用电量
     *
     * @return peak_his_energy - 月峰时段历史用电量
     */
    public Long getPeakHisEnergy() {
        return peakHisEnergy;
    }

    /**
     * 设置月峰时段历史用电量
     *
     * @param peakHisEnergy 月峰时段历史用电量
     */
    public void setPeakHisEnergy(Long peakHisEnergy) {
        this.peakHisEnergy = peakHisEnergy;
    }

    /**
     * 获取月平时段历史用电量
     *
     * @return flat_his_energy - 月平时段历史用电量
     */
    public Long getFlatHisEnergy() {
        return flatHisEnergy;
    }

    /**
     * 设置月平时段历史用电量
     *
     * @param flatHisEnergy 月平时段历史用电量
     */
    public void setFlatHisEnergy(Long flatHisEnergy) {
        this.flatHisEnergy = flatHisEnergy;
    }

    /**
     * 获取月谷时段历史用电量
     *
     * @return valley_his_energy - 月谷时段历史用电量
     */
    public Long getValleyHisEnergy() {
        return valleyHisEnergy;
    }

    /**
     * 设置月谷时段历史用电量
     *
     * @param valleyHisEnergy 月谷时段历史用电量
     */
    public void setValleyHisEnergy(Long valleyHisEnergy) {
        this.valleyHisEnergy = valleyHisEnergy;
    }

    /**
     * 获取月尖时段历史用电量
     *
     * @return spike_his_energy - 月尖时段历史用电量
     */
    public Long getSpikeHisEnergy() {
        return spikeHisEnergy;
    }

    /**
     * 设置月尖时段历史用电量
     *
     * @param spikeHisEnergy 月尖时段历史用电量
     */
    public void setSpikeHisEnergy(Long spikeHisEnergy) {
        this.spikeHisEnergy = spikeHisEnergy;
    }
}