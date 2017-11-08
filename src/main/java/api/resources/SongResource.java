package api.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;

import model.Playlist;
import model.Song;
import model.repository.MapPlaylistRepository;
import model.repository.PlaylistRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("/songs")
public class SongResource {
    
    public static SongResource _instance = null;
    PlaylistRepository repository;
    
    private SongResource() {
        repository = MapPlaylistRepository.getInstance();
    }
    
    public static SongResource getInstance() {
        if (_instance == null) {
            _instance = new SongResource();
        }
        return _instance;
    }
    
    @GET
    @Produces("application/json")
    public Collection<Song> getAll() {
        return repository.getAllSongs();
    }
    
    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Song get(@PathParam("id") String songId) {
        Song resultado = repository.getSong(songId);
        if (resultado == null) {
            throw new NotFoundException("La cancion " + songId + " no existe en la base de datos");
        }
        
        return resultado;
    }
    
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addSong(@Context UriInfo uriInfo, Song song) {
        if (song.getTitle() != null && !song.getTitle().equals("")) {
            repository.addSong(song);
        } else {
            throw new BadRequestException("Las canciones deben tener título");
        }
        URI resultado = null;
        try {
            resultado = new URI("");
        } catch (URISyntaxException ex) {
            Logger.getLogger(SongResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        ResponseBuilder resp = Response.created(resultado);
        resp.entity(song);
        
        return resp.build();
    }
    
    @PUT
    @Consumes("application/json")
    public void updateSong(Song song) {
        Song oldSong = repository.getSong(song.getId());
        if (oldSong == null) {
            throw new NotFoundException("La canción que desea modificar no existe");
        }
        if (song.getTitle() == null || song.getTitle().equals("")) {
            throw new BadRequestException("Las canciones deben tener título");
        }
        repository.updateSong(song);
    }
    
    @DELETE
    @Path("/{id}")
    public void removeSong(@PathParam("id") String songId) {
        Song song = repository.getSong(songId);
        if (song == null) {
            throw new NotFoundException("La canción que desea modificar no existe");
        }
        repository.deleteSong(songId);
        
    }
    
}
