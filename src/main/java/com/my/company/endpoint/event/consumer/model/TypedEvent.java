package com.my.company.endpoint.event.consumer.model;

import com.my.company.PojaGenerated;
import com.my.company.endpoint.event.model.PojaEvent;

@PojaGenerated
public record TypedEvent(String typeName, PojaEvent payload) {}
