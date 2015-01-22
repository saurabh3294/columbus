package com.proptiger.data.notification.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proptiger.data.notification.enums.NotificationStatus;
import com.proptiger.data.notification.model.NotificationGenerated;
import com.proptiger.data.notification.model.NotificationMessage;
import com.proptiger.data.notification.model.NotificationType;
import com.proptiger.data.notification.processor.NotificationPrimaryKeyProcessor;
import com.proptiger.data.notification.processor.dto.NotificationByKeyDto;
import com.proptiger.data.notification.processor.dto.NotificationByTypeDto;
import com.proptiger.data.notification.processor.dto.NotificationProcessorDto;

@Service
public class NotificationProcessorDtoService {

	@Autowired
	private NotificationGeneratedService nGeneratedService;

	@Autowired
	private NotificationMessageService nMessageService;

	public List<NotificationProcessorDto> buildPrimaryKeyDto(
			List<NotificationMessage> nMessages,
			List<NotificationGenerated> nGenerateds) {
		List<NotificationProcessorDto> nIntraProcessorDtos = new ArrayList<NotificationProcessorDto>();
		Map<Integer, NotificationProcessorDto> map = buildDtoWithNMessage(
				nMessages, nIntraProcessorDtos);

		buildDtoWithNGenerated(map, nGenerateds, nIntraProcessorDtos);

		return nIntraProcessorDtos;
	}

	public void buildNonPrimaryKeyDto(NotificationProcessorDto processorDto) {
		List<NotificationMessage> typeMessages, keyMessages;
		List<NotificationGenerated> typeGenerateds, keyGenerateds;
		List<NotificationMessage> typeDiscardMessages, keyDiscardMessages;
		Map<NotificationStatus, List<NotificationGenerated>> typeDiscardGeneratedMap, keyDiscardGeneratedMap;

		NotificationByTypeDto byTypeDto;
		NotificationByKeyDto byKeyDto;

		for (Map.Entry<Integer, NotificationByTypeDto> entry : processorDto
				.getNotificationByTypeDtos().entrySet()) {
			byTypeDto = entry.getValue();
			typeMessages = byTypeDto.getNotificationMessages();
			typeGenerateds = byTypeDto.getNotificationGenerateds();
			typeDiscardMessages = byTypeDto.getDiscardedMessage();
			typeDiscardGeneratedMap = byTypeDto.getDiscardGeneratedMap();

			for (Map.Entry<Object, NotificationByKeyDto> keyEntry : entry
					.getValue().getNotificationMessageByKeys().entrySet()) {
				byKeyDto = keyEntry.getValue();
				keyMessages = byKeyDto.getNotificationMessages();
				keyGenerateds = byKeyDto.getNotificationGenerateds();
				keyDiscardMessages = byKeyDto.getDiscardedMessage();
				keyDiscardGeneratedMap = byKeyDto.getDiscardGeneratedMap();

				typeMessages.addAll(keyMessages);
				typeGenerateds.addAll(keyGenerateds);
				typeDiscardMessages.addAll(keyDiscardMessages);
				typeDiscardGeneratedMap.putAll(keyDiscardGeneratedMap);

				keyMessages.clear();
				keyMessages.clear();
				keyDiscardMessages.clear();
				keyDiscardGeneratedMap.clear();
			}
		}

	}

	private void buildDtoWithNGenerated(
			Map<Integer, NotificationProcessorDto> map,
			List<NotificationGenerated> nGeneratedList,
			List<NotificationProcessorDto> nIntraProcessorDtos) {

		/**
		 * TODO to move the grouping of notification generated by message id
		 * from NotificationByKey's List<notificationgenerated> to Map<integer,
		 * List<noticationGenerated> to avoid confusion of the code which is
		 * currently done down here.
		 */

		/**
		 * Distinct Notification Generated based on the notification Message Id.
		 * As processing of all the notification generated of same notification
		 * message id will be same. Hence , transferring only the distinct one.
		 */
		List<NotificationGenerated> distinctNGenerated = new ArrayList<NotificationGenerated>();
		/**
		 * Grouping of Notification Generated By user Id and Notification
		 * Message Id.
		 */
		Map<Integer, Map<Integer, List<NotificationGenerated>>> groupGeneratedByMessage = nGeneratedService
				.groupNotificationByMessageId(nGeneratedList,
						distinctNGenerated);

		NotificationType nType = null;
		NotificationPrimaryKeyProcessor nKeyProcessor = null;
		Object primaryKeyValue = null;
		Integer userId = null;
		NotificationProcessorDto notificationProcessorDto = null;
		/**
		 * Iterating on the distinct Notification Generated. As this will be one
		 * processed. As all other notification Generated of same type, will be
		 * done.
		 */
		for (NotificationGenerated nGenerated : distinctNGenerated) {
			nType = nGenerated.getNotificationType();
			nKeyProcessor = nType.getNotificationTypeConfig()
					.getPrimaryKeyProcessorObject();
			primaryKeyValue = nGenerated.getObjectId();/*nKeyProcessor
					.getPrimaryKeyOfNotificationMessage(nGenerated
							.getNotificationMessagePayload());*/
			userId = nGenerated.getUserId();

			try {
				NotificationByKeyDto nByKeyDto = createNewDto(map, nType, nGenerated.getNotificationMessage(), primaryKeyValue, nIntraProcessorDtos);
				notificationProcessorDto = map.get(userId);
				// Setting all notification Generated group by message Id in the
				// dto. To be used later on during
				// updating of notification generated.
				notificationProcessorDto
						.setAllNotificationGroupByMessageId(groupGeneratedByMessage
								.get(userId));
				nByKeyDto.getNotificationGenerateds().add(nGenerated);
			} catch (NullPointerException e) {
				continue;
			}

		}
	}

	private Map<Integer, NotificationProcessorDto> buildDtoWithNMessage(
			List<NotificationMessage> nMessages,
			List<NotificationProcessorDto> nIntraProcessorDtoList) {
		Map<Integer, NotificationProcessorDto> map = new LinkedHashMap<Integer, NotificationProcessorDto>();

		NotificationByKeyDto nByKey = null;
		NotificationType nType = null;
		NotificationPrimaryKeyProcessor nKeyProcessor = null;
		Object primaryKeyValue = null;

		for (NotificationMessage nMessage : nMessages) {
			nType = nMessage.getNotificationType();
			nKeyProcessor = nType.getNotificationTypeConfig()
					.getPrimaryKeyProcessorObject();
			primaryKeyValue = nKeyProcessor
					.getPrimaryKeyOfNotificationMessage(nMessage
							.getNotificationMessagePayload());

			nByKey = createNewDto(map, nType, nMessage, primaryKeyValue, nIntraProcessorDtoList);
			nByKey.getNotificationMessages().add(nMessage);
		}

		return map;
	}

	private NotificationByKeyDto createNewDto(
			Map<Integer, NotificationProcessorDto> map,
			NotificationType nType,
			NotificationMessage nMessage,
			Object primaryKeyValue,
			List<NotificationProcessorDto> nIntraProcessorDtoList) {
		
		NotificationProcessorDto notificationIntraProcessorDto = map
				.get(nMessage.getUserId());
		if (notificationIntraProcessorDto == null) {
			notificationIntraProcessorDto = new NotificationProcessorDto();
			notificationIntraProcessorDto.setUserId(nMessage.getUserId());
			nIntraProcessorDtoList.add(notificationIntraProcessorDto);
			map.put(nMessage.getUserId(), notificationIntraProcessorDto);
		}
		Map<Integer, NotificationByTypeDto> typeMap = notificationIntraProcessorDto.getNotificationByTypeDtos();

		NotificationByTypeDto nByTypeDto = typeMap.get(nType.getId());
		if (nByTypeDto == null) {
			nByTypeDto = new NotificationByTypeDto();
			nByTypeDto.setNotificationType(nType);
			typeMap.put(nType.getId(), nByTypeDto);
		}

		Map<Object, NotificationByKeyDto> keyMap = nByTypeDto.getNotificationMessageByKeys();

		NotificationByKeyDto nByKey = keyMap.get(primaryKeyValue);
		if (nByKey == null) {
			nByKey = new NotificationByKeyDto();
			nByKey.setObjectId(primaryKeyValue);
			keyMap.put(primaryKeyValue, nByKey);
		}
		
		return nByKey;
	}

	@Transactional
	// IF NM is empty but NG is present.
	public List<NotificationGenerated> PersistProcessesNotifications(
			NotificationProcessorDto notificationProcessorDto) {

		List<NotificationGenerated> savedNotifications = new ArrayList<NotificationGenerated>();

		NotificationByTypeDto notificationByTypeDto;
		for (Map.Entry<Integer, NotificationByTypeDto> entry : notificationProcessorDto
				.getNotificationByTypeDtos().entrySet()) {

			notificationByTypeDto = entry.getValue();
			// Notification will be generated for these messages.
			List<NotificationMessage> nMessages = notificationByTypeDto
					.getNotificationMessages();
			// Nothing will be happened to them as they are generated which
			// remain unprocessed.
			List<NotificationGenerated> nGenerateds = notificationByTypeDto
					.getNotificationGenerateds();
			// These messages will not be used for notification generation.
			List<NotificationMessage> discardedMessages = notificationByTypeDto
					.getDiscardedMessage();
			// These notification generated will be marked for discarded.
			Map<NotificationStatus, List<NotificationGenerated>> discardGeneratedMap = notificationByTypeDto
					.getDiscardGeneratedMap();

			// updating Notification Generated
			nGeneratedService.updateNotificationGeneratedStatusOnOldStatus(
					discardGeneratedMap, notificationProcessorDto
							.getAllNotificationGroupByMessageId());
			// updating the discard Notification Message
			nMessageService.saveOrUpdateMessages(discardedMessages);

			// check New Notification Messages and Generated them if they did
			// not exists in the database.
			nMessageService.checkAndGenerateNewMessages(nMessages);

			savedNotifications.addAll(nGeneratedService
					.generateNotficationGenerated(nMessages));

			for (NotificationMessage nMessage : nMessages) {
				nMessage.setNotificationStatus(NotificationStatus.Generated);
			}
			nMessageService.saveOrUpdateMessages(nMessages);
		}

		return savedNotifications;
	}

}
