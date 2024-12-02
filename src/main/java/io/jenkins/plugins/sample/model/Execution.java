//import io.jenkins.plugins.sample.model.Testcase;
//
//import java.util.List;
//import java.util.Map;
//
//public class Execution{
//
//    private String _id;
//    private int customerId;
//    private boolean deleted;
//    private int id;
//    private int projectId;
//    private String agentId;
//    private String authkey;
//    private boolean childExecution;
//    private String cloudType;
//    private String code;
//    private String createdAt;
//    private int createdBy;
//    private String environmentType;
//    private String executionCategory;
//    private String executionOS;
//    private String executionStyle;
//    private String executionTime;
//    private String executionType;
//    private String executionTypeCode;
//    private int executionTypeId;
//    private String executionTypeName;
//    private boolean fromAgent;
//    private Map<String, Object> globalConfiguration;
//    private int iterationId;
//    private List<String> iterationsSelected;
//    private String mode;
//    private Integer moduleId; // Nullable
//    private Integer parentExecutionId; // Nullable
//    private int releaseId;
//    private String result;
//    private String status;
//    private List<String> tags; // Nullable
//    private List<Testcase> testcases;
//    private String type;
//    private Integer userstoryId; // Nullable
//    private Metadata metadata;
//
//    public Execution(Execution data, Options options) {
//        this._id = data.get_id();
//        this.customerId = data.getCustomerId();
//        this.deleted = data.isDeleted();
//        this.id = data.getId();
//        this.projectId = data.getProjectId();
//        this.agentId = data.getAgentId();
//        this.authkey = data.getAuthkey();
//        this.childExecution = data.isChildExecution();
//        this.cloudType = data.getCloudType();
//        this.code = data.getCode();
//        this.createdAt = data.getCreatedAt();
//        this.createdBy = data.getCreatedBy();
//        this.environmentType = data.getEnvironmentType();
//        this.executionCategory = data.getExecutionCategory();
//        this.executionOS = data.getExecutionOS();
//        this.executionStyle = data.getExecutionStyle();
//        this.executionTime = data.getExecutionTime();
//        this.executionType = data.getExecutionType();
//        this.executionTypeCode = data.getExecutionTypeCode();
//        this.executionTypeId = data.getExecutionTypeId();
//        this.executionTypeName = data.getExecutionTypeName();
//        this.fromAgent = data.isFromAgent();
//        this.globalConfiguration = data.getGlobalConfiguration();
//        this.iterationId = data.getIterationId();
//        this.iterationsSelected = data.getIterationsSelected();
//        this.mode = data.getMode();
//        this.moduleId = data.getModuleId();
//        this.parentExecutionId = data.getParentExecutionId();
//        this.releaseId = data.getReleaseId();
//        this.result = data.getResult();
//        this.status = data.getStatus();
//        this.tags = data.getTags();
//        this.testcases = data.getTestcases();
//        this.type = data.getType();
//        this.userstoryId = data.getUserstoryId();
//
//        double threshold = options.getThreshold() != null ? options.getThreshold() : 100.0;
//        boolean verbose = options.isVerbose();
//        this.metadata = new Metadata(threshold, verbose, this);
//    }
//
//    public String get_id() {
//        return _id;
//    }
//
//    public void set_id(String _id) {
//        this._id = _id;
//    }
//
//    public int getCustomerId() {
//        return customerId;
//    }
//
//    public void setCustomerId(int customerId) {
//        this.customerId = customerId;
//    }
//
//    public boolean isDeleted() {
//        return deleted;
//    }
//
//    public void setDeleted(boolean deleted) {
//        this.deleted = deleted;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public int getProjectId() {
//        return projectId;
//    }
//
//    public void setProjectId(int projectId) {
//        this.projectId = projectId;
//    }
//
//    public String getAgentId() {
//        return agentId;
//    }
//
//    public void setAgentId(String agentId) {
//        this.agentId = agentId;
//    }
//
//    public String getAuthkey() {
//        return authkey;
//    }
//
//    public void setAuthkey(String authkey) {
//        this.authkey = authkey;
//    }
//
//    public boolean isChildExecution() {
//        return childExecution;
//    }
//
//    public void setChildExecution(boolean childExecution) {
//        this.childExecution = childExecution;
//    }
//
//    public String getCloudType() {
//        return cloudType;
//    }
//
//    public void setCloudType(String cloudType) {
//        this.cloudType = cloudType;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public int getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(int createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public String getEnvironmentType() {
//        return environmentType;
//    }
//
//    public void setEnvironmentType(String environmentType) {
//        this.environmentType = environmentType;
//    }
//
//    public String getExecutionCategory() {
//        return executionCategory;
//    }
//
//    public void setExecutionCategory(String executionCategory) {
//        this.executionCategory = executionCategory;
//    }
//
//    public String getExecutionOS() {
//        return executionOS;
//    }
//
//    public void setExecutionOS(String executionOS) {
//        this.executionOS = executionOS;
//    }
//
//    public String getExecutionStyle() {
//        return executionStyle;
//    }
//
//    public void setExecutionStyle(String executionStyle) {
//        this.executionStyle = executionStyle;
//    }
//
//    public String getExecutionTime() {
//        return executionTime;
//    }
//
//    public void setExecutionTime(String executionTime) {
//        this.executionTime = executionTime;
//    }
//
//    public String getExecutionType() {
//        return executionType;
//    }
//
//    public void setExecutionType(String executionType) {
//        this.executionType = executionType;
//    }
//
//    public String getExecutionTypeCode() {
//        return executionTypeCode;
//    }
//
//    public void setExecutionTypeCode(String executionTypeCode) {
//        this.executionTypeCode = executionTypeCode;
//    }
//
//    public int getExecutionTypeId() {
//        return executionTypeId;
//    }
//
//    public void setExecutionTypeId(int executionTypeId) {
//        this.executionTypeId = executionTypeId;
//    }
//
//    public String getExecutionTypeName() {
//        return executionTypeName;
//    }
//
//    public void setExecutionTypeName(String executionTypeName) {
//        this.executionTypeName = executionTypeName;
//    }
//
//    public boolean isFromAgent() {
//        return fromAgent;
//    }
//
//    public void setFromAgent(boolean fromAgent) {
//        this.fromAgent = fromAgent;
//    }
//
//    public Map<String, Object> getGlobalConfiguration() {
//        return globalConfiguration;
//    }
//
//    public void setGlobalConfiguration(Map<String, Object> globalConfiguration) {
//        this.globalConfiguration = globalConfiguration;
//    }
//
//    public int getIterationId() {
//        return iterationId;
//    }
//
//    public void setIterationId(int iterationId) {
//        this.iterationId = iterationId;
//    }
//
//    public List<String> getIterationsSelected() {
//        return iterationsSelected;
//    }
//
//    public void setIterationsSelected(List<String> iterationsSelected) {
//        this.iterationsSelected = iterationsSelected;
//    }
//
//    public String getMode() {
//        return mode;
//    }
//
//    public void setMode(String mode) {
//        this.mode = mode;
//    }
//
//    public Integer getModuleId() {
//        return moduleId;
//    }
//
//    public void setModuleId(Integer moduleId) {
//        this.moduleId = moduleId;
//    }
//
//    public Integer getParentExecutionId() {
//        return parentExecutionId;
//    }
//
//    public void setParentExecutionId(Integer parentExecutionId) {
//        this.parentExecutionId = parentExecutionId;
//    }
//
//    public int getReleaseId() {
//        return releaseId;
//    }
//
//    public void setReleaseId(int releaseId) {
//        this.releaseId = releaseId;
//    }
//
//    public String getResult() {
//        return result;
//    }
//
//    public void setResult(String result) {
//        this.result = result;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public List<String> getTags() {
//        return tags;
//    }
//
//    public void setTags(List<String> tags) {
//        this.tags = tags;
//    }
//
//    public List<Testcase> getTestcases() {
//        return testcases;
//    }
//
//    public void setTestcases(List<Testcase> testcases) {
//        this.testcases = testcases;
//    }
//
//    public String getType() {
//        return type;
//    }
//
//    public void setType(String type) {
//        this.type = type;
//    }
//
//    public Integer getUserstoryId() {
//        return userstoryId;
//    }
//
//    public void setUserstoryId(Integer userstoryId) {
//        this.userstoryId = userstoryId;
//    }
//
//    public Metadata getMetadata() {
//        return metadata;
//    }
//
//    public void setMetadata(Metadata metadata) {
//        this.metadata = metadata;
//    }
//// Getters and setters for each field (not shown for brevity)
//    // Implement equals(), hashCode(), and toString() if necessary
//
//    public static class Options {
//        private Double threshold;
//        private Boolean verbose;
//
//        public Options(Double threshold, Boolean verbose) {
//            this.threshold = threshold;
//            this.verbose = verbose;
//        }
//
//        public Double getThreshold() {
//            return threshold;
//        }
//
//        public Boolean isVerbose() {
//            return verbose;
//        }
//    }
//}
