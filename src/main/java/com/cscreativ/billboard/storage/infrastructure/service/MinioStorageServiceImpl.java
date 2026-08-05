package com.cscreativ.billboard.storage.infrastructure.service;

import com.cscreativ.billboard.storage.domain.exception.StorageOperationException;
import com.cscreativ.billboard.storage.domain.service.PhysicalStorageService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
public class MinioStorageServiceImpl implements PhysicalStorageService {

    private final MinioClient minioClient;

    @Value("${storage.minio.bucket-name}")
    private String bucketName;

    @Value("${storage.minio.public-url}")
    private String baseUrl;

    public MinioStorageServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public String store(byte[] content, String filename) {
        try {
            String objectName = UUID.randomUUID() + "_" + filename;
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(new ByteArrayInputStream(content), content.length, -1)
                    .build()
            );
            return objectName;
        } catch (Exception e) {
            throw new StorageOperationException("Erreur lors du transfert vers MinIO", e);
        }
    }

    @Override
    public byte[] retrieve(String storagePath) {
        try {
            return minioClient.getObject(
                io.minio.GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storagePath)
                    .build()
            ).readAllBytes();
        } catch (Exception e) {
            throw new StorageOperationException("Erreur lors de la récupération depuis MinIO", e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(storagePath)
                    .build()
            );
        } catch (Exception e) {
            throw new StorageOperationException("Erreur lors de la suppression sur MinIO", e);
        }
    }

    @Override
    public String generatePublicUrl(String storagePath) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(storagePath)
                    .expiry(60 * 60)
                    .build()
            );
        } catch (Exception e) {
            return baseUrl + storagePath;
        }
    }
}
