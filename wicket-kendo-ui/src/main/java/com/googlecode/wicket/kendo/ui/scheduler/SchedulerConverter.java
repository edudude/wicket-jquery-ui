/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.wicket.kendo.ui.scheduler;

import java.util.List;

import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.wicket.jquery.core.utils.DateUtils;
import com.googlecode.wicket.jquery.core.utils.JsonUtils;
import com.googlecode.wicket.kendo.ui.scheduler.resource.ResourceList;

/**
 * Default implementation of {@link ISchedulerConverter}
 * 
 * @author Sebastien Briquet - sebfz1
 *
 */
public class SchedulerConverter implements ISchedulerConverter
{
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(SchedulerConverter.class);

	@Override
	public JSONObject toJson(SchedulerEvent event)
	{
		try
		{
			JSONObject object = new JSONObject();

			object.put("id", event.getId()); // Object
			object.put("isAllDay", event.isAllDay());
			object.putOpt("title", event.getTitle()); // may be null
			object.putOpt("description", event.getDescription()); // may be null

			if (event.getStart() != null)
			{
				object.put("start", DateUtils.toUTCString(event.getStart()));
			}

			if (event.getEnd() != null)
			{
				object.put("end", DateUtils.toUTCString(event.getEnd()));
			}

			// recurrence //
			object.putOpt("recurrenceId", event.getRecurrenceId()); // may be null
			object.putOpt("recurrenceRule", event.getRecurrenceRule()); // may be null
			object.putOpt("recurrenceException", event.getRecurrenceException()); // may be null

			// resources //
			for (String field : event.getFields())
			{
				object.put(field, event.getValue(field)); // value is type of Object
			}

			return object;
		}
		catch (JSONException e)
		{
			LOG.error(e.getMessage(), e);
		}

		return null;
	}

	@Override
	public SchedulerEvent toObject(JSONObject object, List<ResourceList> lists)
	{
		try
		{
			SchedulerEvent event = this.newSchedulerEvent();
			event.setId(object.get("id")); // Object
			event.setTitle(object.optString("title"));
			event.setDescription(object.optString("description"));

			event.setStart(object.getLong("start"));
			event.setEnd(object.getLong("end"));
			event.setAllDay(object.getBoolean("isAllDay"));

			event.setRecurrenceId(object.optString("recurrenceId"));
			event.setRecurrenceRule(object.optString("recurrenceRule"));
			event.setRecurrenceException(object.optString("recurrenceException"));

			// Resources //
			for (ResourceList list : lists)
			{
				String field = list.getField();
				Object value = object.opt(field);

				if (list.isMultiple() && value instanceof JSONArray)
				{
					event.setValue(field, JsonUtils.toList((JSONArray) value));
				}
				else
				{
					event.setValue(field, value);
				}
			}

			return event;
		}
		catch (JSONException e)
		{
			LOG.error(e.getMessage(), e);
		}

		return null;
	}

	/**
	 * Gets a new {@link SchedulerEvent}
	 * 
	 * @return a new {@link SchedulerEvent}
	 */
	protected SchedulerEvent newSchedulerEvent()
	{
		return new SchedulerEvent();
	}
}
