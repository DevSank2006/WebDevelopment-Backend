package com.webdev.webdevelopment.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(@NotNull
                                 Long serviceId,

                               @NotNull
                                 @Min(1)
                                 Integer quantity) {

}
