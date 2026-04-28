package aiss.videominer.controller;

import aiss.videominer.exceptions.ChannelNotFoundException;
import aiss.videominer.model.Channel;
import aiss.videominer.repository.ChannelRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name="Channels", description = "Channel management API")
@RestController
@RequestMapping("/videominer")
public class ChannelController {

    @Autowired
    ChannelRepository channelRepository;

    @ApiResponse(responseCode = "200", description = "Listado de canales",
            content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Channel.class)), mediaType = "application/json")})
    @Operation(summary = "Retrieve all Channels", description = "Get a list of all Channel objects", tags = {"get", "channels"})
    @GetMapping("/channels")
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canal obtenido",
                    content = {@Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Canal no encontrado")
    })
    @Operation(summary = "Retrieve a Channel",
            description = "Get a Channel object by specifying its Id", tags = {"get", "channel"})
    @GetMapping("/channels/{channelId}")
    public Channel findOne(@Parameter(description = "id from channel to be searched")
                           @PathVariable(value = "channelId") long channelId) throws ChannelNotFoundException {
        Optional<Channel> channel = channelRepository.findById(channelId);
        if(!channel.isPresent()){
            throw new ChannelNotFoundException();
        }
        return channel.get();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Canal creado",
                    content = {@Content(schema = @Schema(implementation = Channel.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = {@Content(schema = @Schema())})
    })
    @Operation(summary = "Create a Channel", description = "Create a Channel object by its attributes", tags = {"channels", "post"})
    @PostMapping("/channels)")
    @ResponseStatus(HttpStatus.CREATED)
    public Channel createChannel(@Parameter(description = "data of channel to be created") @RequestBody @Valid Channel channel) {
        return channelRepository.save(channel);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Canal actualizado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Canal no encontrado", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = {@Content(schema = @Schema())})
    })
    @Operation(summary = "Update a Channel", description = "Update a Channel object by specifying its Id", tags = {"channels", "put"})
    @PutMapping("/channels/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannel(@Parameter(description = "data of channel to be updated") @RequestBody @Valid Channel channel,
                              @Parameter(description = "id of channel to be updated") @PathVariable("channelId") long channelId) throws ChannelNotFoundException {
        Optional<Channel> channelData = channelRepository.findById(channelId);
        if(!channelData.isPresent()){
            throw new ChannelNotFoundException();
        }
        Channel newChannel = channelData.get();
        newChannel.setName(channel.getName());
        newChannel.setDescription(channel.getDescription());
        newChannel.setCreatedTime(channel.getCreatedTime());
        newChannel.setVideos(channel.getVideos());
        channelRepository.save(newChannel);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Canal borrado",
                    content = {@Content(schema = @Schema())}),
    })
    @Operation(summary = "Delete a Channel by Id", description = "Delete a Channel object by specifying its Id", tags = {"channels", "delete"})
    @DeleteMapping("/channels/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannel(@Parameter(description = "id of channel to be deleted") @PathVariable("channelId") long channelId) {
        if (channelRepository.existsById(channelId)) {
            channelRepository.deleteById(channelId);
        }
    }
}
