package com.programm.vertx.handler;

import com.programm.vertx.dto.UserDto;
import com.programm.vertx.dto.UserInput;
import com.programm.vertx.http.ResponseHelper;
import com.programm.vertx.http.StatusCodes;
import com.programm.vertx.repository.IRepository;
import com.programm.vertx.response.*;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.stream.Collectors;

public class UsersHandler {
    private final IRepository<UserDto> repository;

    public UsersHandler(IRepository<UserDto> repository) {
        this.repository = repository;
    }

    public void getAll(RoutingContext ctx) {
        String startsFrom = ctx.request().getParam("startsFrom");
        String limitStr = ctx.request().getParam("limit");
        String offsetStr = ctx.request().getParam("offset");

        int limit = 10;
        int offset = 0;

        if (limitStr != null) {
            try {
                limit = Integer.parseInt(limitStr);
            } catch (NumberFormatException ignored) {
            }
        }

        if (offsetStr != null) {
            try {
                offset = Integer.parseInt(offsetStr);
            } catch (NumberFormatException ignored) {
            }
        }

        Map<String, UserDto> result = repository.findAll();

        if (startsFrom != null) {
            String startsFromLowerCase = startsFrom.toLowerCase();
            result = result.entrySet().stream()
                    .filter(entry -> entry.getValue().getLogin().toLowerCase().startsWith(startsFromLowerCase))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        int total = result.size();

        Map<String, UserResponse> responseAll = result.entrySet().stream().skip(offset).limit(limit)
                .map(entry -> Map.entry(entry.getKey(), UserResponse.from(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        ResponseHelper.json(ctx.response(), new ResponseWrapper<>(responseAll,
                new Pagination(total, offset, limit))
        );
    }

    public void create(RoutingContext ctx) {
        RequestBody body = ctx.body();
        UserInput userInput = body.asJsonObject().mapTo(UserInput.class);
        UserDto dto = repository.add(UserDto.from(userInput));

        ResponseHelper.json(ctx.response(), UserResponse.from(dto));
    }

    public void get(RoutingContext ctx) {
        UserDto dto = repository.find(ctx.pathParam("id"));
        if (dto == null) {
            ResponseHelper.json(ctx.response(), StatusCodes.NOT_FOUND, new ErrorResponseWrapper(ErrorResponse.NOT_FOUND()));
            return;
        }
        ResponseHelper.json(ctx.response(), UserResponse.from(dto));
    }

    public void put(RoutingContext ctx) {
        String uuid = ctx.pathParam("id");
        UserDto dto = repository.find(uuid);

        if (dto == null) {
            ResponseHelper.json(ctx.response(), StatusCodes.NOT_FOUND, new ErrorResponseWrapper(ErrorResponse.NOT_FOUND()));
            return;
        }
        UserDto from = UserDto.from(ctx.body().asJsonObject().mapTo(UserInput.class));
        from.setId(uuid);
        repository.update(from);

        ResponseHelper.json(ctx.response(), UserResponse.from(from));
    }

    public void delete(RoutingContext ctx) {
        UserDto dto = repository.find(ctx.pathParam("id"));
        if (dto == null) {
            ResponseHelper.json(ctx.response(), StatusCodes.NOT_FOUND, new ErrorResponseWrapper(ErrorResponse.NOT_FOUND()));
            return;
        }
        repository.delete(dto);

        ctx.response().setStatusCode(StatusCodes.NO_CONTENT).end();
    }

}
