package com.rabbitq.models;

import com.rabbitq.entity.TargetOptionsEntity;

import java.util.Set;

@FunctionalInterface
public interface SubDomainInterface {
    Set<String> getSubDomain(TargetOptionsEntity targetOptionsEntity) throws Exception;
}