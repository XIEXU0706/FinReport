package org.example.finreport.common.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssService {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    public String upload(String objectKey, byte[] data) {
        ossClient.putObject(bucketName, objectKey, new ByteArrayInputStream(data));
        return getUrl(objectKey);
    }

    public InputStream download(String objectKey) {
        return ossClient.getObject(bucketName, objectKey).getObjectContent();
    }

    public void delete(String objectKey) {
        ossClient.deleteObject(bucketName, objectKey);
    }

    public void deleteMulti(String... objectKeys) {
        ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(Arrays.asList(objectKeys)));
    }

    public String getUrl(String objectKey) {
        return "https://" + bucketName + "." + endpoint.replace("https://", "") + "/" + objectKey;
    }

    public String buildKey(String reportType, Long reportId) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return "reports/" + reportType + "/" + date + "_report_" + reportId + ".xlsx";
    }
}
