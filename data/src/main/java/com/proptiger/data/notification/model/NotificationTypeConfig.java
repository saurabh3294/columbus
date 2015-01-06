package com.proptiger.data.notification.model;

import java.util.HashMap;
import java.util.Map;

import com.proptiger.data.notification.enums.NotificationTypeEnum;
import com.proptiger.data.notification.model.payload.NotificationTypePayload;
import com.proptiger.data.notification.processor.NotificationNonPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.message.DefaultNotificationMessageProcessor;
import com.proptiger.data.notification.processor.message.GoalPriceNotificationMessageProcessor;
import com.proptiger.data.notification.processor.message.LocalityNewsNotificationMessageProcessor;
import com.proptiger.data.notification.processor.message.NotificationMessageProcessor;
import com.proptiger.data.notification.processor.message.PhotoAddNotificationMessageProcessor;
import com.proptiger.data.notification.processor.message.PriceChangeNotificationMessageProcessor;
import com.proptiger.data.notification.processor.message.ProjectNewsNotificationMessageProcessor;
import com.proptiger.data.notification.processor.type.DefaultNotificationTypeProcessor;
import com.proptiger.data.notification.processor.type.NewsNotificationTypeProcessor;
import com.proptiger.data.notification.processor.type.NotificationTypeProcessor;
import com.proptiger.data.notification.processor.type.PriceChangeNotificationTypeProcessor;
import com.proptiger.data.notification.sender.data.DefaultTemplateDataFetcher;
import com.proptiger.data.notification.sender.data.MonthlyUpdateTemplateDataFetcher;
import com.proptiger.data.notification.sender.data.TemplateDataFetcher;

public class NotificationTypeConfig {

    public transient static Map<String, NotificationTypeConfig>           notificationTypeConfigMap;

    static {
        notificationTypeConfigMap = new HashMap<String, NotificationTypeConfig>();

        notificationTypeConfigMap.put(NotificationTypeEnum.PortfolioPriceChange.getName(), new NotificationTypeConfig(
                NotificationTypePayload.class,
                PriceChangeNotificationTypeProcessor.class,
                NotificationPrimaryKeyProcessor.class,
                NotificationNonPrimaryKeyProcessor.class,
                PriceChangeNotificationMessageProcessor.class,
                DefaultTemplateDataFetcher.class));

        notificationTypeConfigMap.put(NotificationTypeEnum.PortfolioGoalPrice.getName(), new NotificationTypeConfig(
                NotificationTypePayload.class,
                PriceChangeNotificationTypeProcessor.class,
                NotificationPrimaryKeyProcessor.class,
                NotificationNonPrimaryKeyProcessor.class,
                GoalPriceNotificationMessageProcessor.class,
                DefaultTemplateDataFetcher.class));

        notificationTypeConfigMap.put(NotificationTypeEnum.PortfolioPhotoAdd.getName(), new NotificationTypeConfig(
                NotificationTypePayload.class,
                DefaultNotificationTypeProcessor.class,
                NotificationPrimaryKeyProcessor.class,
                NotificationNonPrimaryKeyProcessor.class,
                PhotoAddNotificationMessageProcessor.class,
                DefaultTemplateDataFetcher.class));

        notificationTypeConfigMap.put(NotificationTypeEnum.PortfolioProjectNews.getName(), new NotificationTypeConfig(
                NotificationTypePayload.class,
                NewsNotificationTypeProcessor.class,
                NotificationPrimaryKeyProcessor.class,
                NotificationNonPrimaryKeyProcessor.class,
                ProjectNewsNotificationMessageProcessor.class,
                DefaultTemplateDataFetcher.class));

        notificationTypeConfigMap.put(NotificationTypeEnum.PortfolioLocalityNews.getName(), new NotificationTypeConfig(
                NotificationTypePayload.class,
                NewsNotificationTypeProcessor.class,
                NotificationPrimaryKeyProcessor.class,
                NotificationNonPrimaryKeyProcessor.class,
                LocalityNewsNotificationMessageProcessor.class,
                DefaultTemplateDataFetcher.class));

        notificationTypeConfigMap.put(
                NotificationTypeEnum.PortfolioMonthlyUpdates.getName(),
                new NotificationTypeConfig(
                        NotificationTypePayload.class,
                        DefaultNotificationTypeProcessor.class,
                        NotificationPrimaryKeyProcessor.class,
                        NotificationNonPrimaryKeyProcessor.class,
                        DefaultNotificationMessageProcessor.class,
                        MonthlyUpdateTemplateDataFetcher.class));
    }

    private transient Class<? extends NotificationTypePayload>            dataClassName                         = NotificationTypePayload.class;
    private transient Class<? extends NotificationTypeProcessor>          notificationTypeProcessorClassName    = DefaultNotificationTypeProcessor.class;
    private transient Class<? extends NotificationPrimaryKeyProcessor>    primaryKeyProcessorClassName          = NotificationPrimaryKeyProcessor.class;
    private transient Class<? extends NotificationNonPrimaryKeyProcessor> nonPrimaryKeyProcessorClassName       = NotificationNonPrimaryKeyProcessor.class;
    private transient Class<? extends NotificationMessageProcessor>       notificationMessageProcessorClassName = DefaultNotificationMessageProcessor.class;
    private transient Class<? extends TemplateDataFetcher>                templateDataFetcherClassName          = DefaultTemplateDataFetcher.class;

    private transient NotificationPrimaryKeyProcessor                     primaryKeyProcessorObject;
    private transient NotificationNonPrimaryKeyProcessor                  nonPrimaryKeyProcessorObject;
    private transient NotificationTypePayload                             notificationTypePayloadObject;
    private transient NotificationTypeProcessor                           notificationTypeProcessorObject;
    private transient NotificationMessageProcessor                        notificationMessageProcessorObject;
    private transient TemplateDataFetcher                                 templateDataFetcherObject;

    public NotificationTypeConfig(
            Class<? extends NotificationTypePayload> dataClassName,
            Class<? extends NotificationTypeProcessor> notificationTypeProcessorClassName,
            Class<? extends NotificationPrimaryKeyProcessor> primaryKeyProcessorClassName,
            Class<? extends NotificationNonPrimaryKeyProcessor> nonPrimaryKeyProcessorClassName,
            Class<? extends NotificationMessageProcessor> notificationMessageProcessorClassName,
            Class<? extends TemplateDataFetcher> templateDataFetcherClassName) {

        super();
        if (dataClassName != null) {
            this.dataClassName = dataClassName;
        }
        if (notificationTypeProcessorClassName != null) {
            this.notificationTypeProcessorClassName = notificationTypeProcessorClassName;
        }
        if (primaryKeyProcessorClassName != null) {
            this.primaryKeyProcessorClassName = primaryKeyProcessorClassName;
        }
        if (nonPrimaryKeyProcessorClassName != null) {
            this.nonPrimaryKeyProcessorClassName = nonPrimaryKeyProcessorClassName;
        }
        if (notificationMessageProcessorClassName != null) {
            this.notificationMessageProcessorClassName = notificationMessageProcessorClassName;
        }
        if (templateDataFetcherClassName != null) {
            this.templateDataFetcherClassName = templateDataFetcherClassName;
        }
    }

    public NotificationTypeConfig() {
        // TODO Auto-generated constructor stub
    }

    public Class<? extends NotificationTypePayload> getDataClassName() {
        return dataClassName;
    }

    public void setDataClassName(Class<? extends NotificationTypePayload> dataClassName) {
        this.dataClassName = dataClassName;
    }

    public NotificationTypePayload getNotificationTypePayloadObject() {
        return notificationTypePayloadObject;
    }

    public void setNotificationTypePayloadObject(NotificationTypePayload notificationTypePayloadObject) {
        this.notificationTypePayloadObject = notificationTypePayloadObject;
    }

    public static Map<String, NotificationTypeConfig> getNotificationTypeConfigMap() {
        return notificationTypeConfigMap;
    }

    public static void setNotificationTypeConfigMap(Map<String, NotificationTypeConfig> notificationTypeConfigMap) {
        NotificationTypeConfig.notificationTypeConfigMap = notificationTypeConfigMap;
    }

    public Class<? extends NotificationPrimaryKeyProcessor> getPrimaryKeyProcessorClassName() {
        return primaryKeyProcessorClassName;
    }

    public void setPrimaryKeyProcessorClassName(
            Class<? extends NotificationPrimaryKeyProcessor> primaryKeyProcessorClassName) {
        this.primaryKeyProcessorClassName = primaryKeyProcessorClassName;
    }

    public Class<? extends NotificationNonPrimaryKeyProcessor> getNonPrimaryKeyProcessorClassName() {
        return nonPrimaryKeyProcessorClassName;
    }

    public void setNonPrimaryKeyProcessorClassName(
            Class<? extends NotificationNonPrimaryKeyProcessor> nonPrimaryKeyProcessorClassName) {
        this.nonPrimaryKeyProcessorClassName = nonPrimaryKeyProcessorClassName;
    }

    public NotificationPrimaryKeyProcessor getPrimaryKeyProcessorObject() {
        return primaryKeyProcessorObject;
    }

    public void setPrimaryKeyProcessorObject(NotificationPrimaryKeyProcessor primaryKeyProcessorObject) {
        this.primaryKeyProcessorObject = primaryKeyProcessorObject;
    }

    public NotificationNonPrimaryKeyProcessor getNonPrimaryKeyProcessorObject() {
        return nonPrimaryKeyProcessorObject;
    }

    public void setNonPrimaryKeyProcessorObject(NotificationNonPrimaryKeyProcessor nonPrimaryKeyProcessorObject) {
        this.nonPrimaryKeyProcessorObject = nonPrimaryKeyProcessorObject;
    }

    public Class<? extends NotificationMessageProcessor> getNotificationMessageProcessorClassName() {
        return notificationMessageProcessorClassName;
    }

    public void setNotificationMessageProcessorClassName(
            Class<? extends NotificationMessageProcessor> notificationMessageProcessorClassName) {
        this.notificationMessageProcessorClassName = notificationMessageProcessorClassName;
    }

    public NotificationMessageProcessor getNotificationMessageProcessorObject() {
        return notificationMessageProcessorObject;
    }

    public void setNotificationMessageProcessorObject(NotificationMessageProcessor notificationMessageProcessorObject) {
        this.notificationMessageProcessorObject = notificationMessageProcessorObject;
    }

    public Class<? extends NotificationTypeProcessor> getNotificationTypeProcessorClassName() {
        return notificationTypeProcessorClassName;
    }

    public void setNotificationTypeProcessorClassName(
            Class<? extends NotificationTypeProcessor> notificationTypeProcessorClassName) {
        this.notificationTypeProcessorClassName = notificationTypeProcessorClassName;
    }

    public NotificationTypeProcessor getNotificationTypeProcessorObject() {
        return notificationTypeProcessorObject;
    }

    public void setNotificationTypeProcessorObject(NotificationTypeProcessor notificationTypeProcessorObject) {
        this.notificationTypeProcessorObject = notificationTypeProcessorObject;
    }

    public Class<? extends TemplateDataFetcher> getTemplateDataFetcherClassName() {
        return templateDataFetcherClassName;
    }

    public void setTemplateDataFetcherClassName(Class<? extends TemplateDataFetcher> templateDataFetcherClassName) {
        this.templateDataFetcherClassName = templateDataFetcherClassName;
    }

    public TemplateDataFetcher getTemplateDataFetcherObject() {
        return templateDataFetcherObject;
    }

    public void setTemplateDataFetcherObject(TemplateDataFetcher templateDataFetcherObject) {
        this.templateDataFetcherObject = templateDataFetcherObject;
    }

}
