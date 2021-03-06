package pl.com.sages.spring.io.deal.game

import groovy.transform.TypeChecked
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.com.sages.spring.io.deal.data.GenericResourceAssembler
import pl.com.sages.spring.io.deal.game.door.DoorResource

import javax.validation.constraints.NotNull

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import static org.springframework.web.bind.annotation.RequestMethod.GET
import static org.springframework.web.bind.annotation.RequestMethod.POST

@TypeChecked
@Validated
@RestController
@RequestMapping("/games")
class GameResource extends GenericResourceAssembler<Game> {

    private final GameService service

    @Autowired
    GameResource(GameService service) {
        super(GameResource)
        this.service = service
    }

    @RequestMapping(value = "", method = GET)
    Page<Resource<Game>> search(Pageable pageRequest) {
        return toResourcesPage(service.search(pageRequest));
    }

    @RequestMapping(value = "", method = POST)
    ResponseEntity<Void> create() {
        Game game = service.create()
        return respondCreated(game)
    }

    @RequestMapping(value = "/{game}", method = GET)
    Resource<Game> read(@NotNull @PathVariable Game game) {
        return toResource(game)
    }

    @Override
    Resource<Game> toResource(Game game) {
        Resource<Game> resource = createResourceWithId(game)
        (0..<game.doors.size()).each { int idx ->
            resource.add(linkTo(DoorResource, game.id).slash(idx).withRel("door"));
        }
        return resource;
    }
}
