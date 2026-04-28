package aiss.videominer.controller;

import aiss.videominer.exceptions.CaptionNotFoundException;
import aiss.videominer.exceptions.VideoNotFoundException;
import aiss.videominer.model.Caption;
import aiss.videominer.model.Video;
import aiss.videominer.repository.CaptionRepository;
import aiss.videominer.repository.VideoRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Tag(name="Caption", description = "Caption management API")
@RestController
@RequestMapping("/videominer")
public class CaptionController {

    @Autowired
    CaptionRepository captionRepository;
    @Autowired
    VideoRepository videoRepository;

    @ApiResponse(responseCode = "200", description = "Listado de captions",
            content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Caption.class)), mediaType = "application/json")})
    @Operation(summary = "Retrieve all Captions", description = "Get a list of all Caption objects", tags = {"get", "captions"})
    @GetMapping("/captions")
    public List<Caption> findAll() {
        return captionRepository.findAll();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Caption obtenido",
                    content = {@Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Caption no encontrado")
    })
    @Operation(summary = "Retrieve a Caption",
            description = "Get a Caption object by specifying its Id", tags = {"get", "caption"})
    @GetMapping("/caption/{captionId}")
    public Caption findOne(@Parameter(description = "id from caption to be searched")
                           @PathVariable(value = "captionId") long captionId) throws CaptionNotFoundException {
        Optional<Caption> caption = captionRepository.findById(captionId);
        if(!caption.isPresent()){
            throw new CaptionNotFoundException();
        }
        return caption.get();
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de captions de un video",
                    content = {@Content(array = @ArraySchema(schema = @Schema(implementation = Caption.class)), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Video no encontrado", content = {@Content(schema = @Schema())})
    })
    @Operation(summary = "Retrieve all Captions from Video",
            description = "Get a list of all Caption objects from an Video object by specifying its Id", tags = {"get", "videos", "captions"})
    @GetMapping("/videos/{videoId}/captions")
    public List<Caption> getAllCaptionsByVideoId(@Parameter(description = "id from video whose captions needs to be listed")
                                                 @PathVariable(value = "videoId") long videoId) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(videoId);
        if(!video.isPresent()){
            throw new VideoNotFoundException();
        }
        List<Caption> captions = new ArrayList<>(video.get().getCaptions());
        return captions;
    }

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Caption creado",
                    content = {@Content(schema = @Schema(implementation = Caption.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Video no encontrado", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = {@Content(schema = @Schema())})
    })
    @Operation(summary = "Create a Video Caption", description = "Create a Caption object by its attributes in a Video object by specifying its Id", tags = {"captions", "videos", "post"})
    @PostMapping("/videos/{videoId}/captions)")
    @ResponseStatus(HttpStatus.CREATED)
    public Caption createCaption(@Parameter(description = "data of caption to be created") @RequestBody @Valid Caption caption,
                                 @Parameter(description = "video where the caption will be uploaded") @PathVariable("videoId") long videoId) throws VideoNotFoundException {
        Optional<Video> video = videoRepository.findById(videoId);
        if(!video.isPresent()){
            throw new VideoNotFoundException();
        }
        video.get().getCaptions().add(caption);
        return captionRepository.save(caption);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Caption actualizado",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "404", description = "Caption no encontrado", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = {@Content(schema = @Schema())})
    })
    @Operation(summary = "Update a Caption", description = "Update a Caption object by specifying its Id", tags = {"captions", "put"})
    @PutMapping("/captions/{captionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCaption(@Parameter(description = "data of caption to be updated") @RequestBody @Valid Caption caption,
                            @Parameter(description = "id of caption to be updated") @PathVariable("captionId") long captionId) throws CaptionNotFoundException {
        Optional<Caption> captionData = captionRepository.findById(captionId);
        if(!captionData.isPresent()){
            throw new CaptionNotFoundException();
        }
        Caption newCaption = captionData.get();
        newCaption.setName(caption.getName());
        newCaption.setLanguage(caption.getLanguage());
        captionRepository.save(newCaption);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Caption borrado",
                    content = {@Content(schema = @Schema())}),
    })
    @Operation(summary = "Delete a Caption by Id", description = "Delete a Caption object by specifying its Id", tags = {"captions", "delete"})
    @DeleteMapping("/captions/{captionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCaption(@Parameter(description = "id of caption to be deleted") @PathVariable("captionId") long captionId) {
        if (captionRepository.existsById(captionId)) {
            captionRepository.deleteById(captionId);
        }
    }
}
