package com.co.MD.PPCTM.Services;

import com.co.MD.PPCTM.Domain.EntityCancion;
import com.co.MD.PPCTM.Repository.RepositoryPlayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicePlayList {

    @Autowired
    RepositoryPlayList repositoryPlayList;

    private Boolean validacionesAgregarCancion(EntityCancion cancion) {


        if (cancion == null) {
            return false;
        }
        if (cancion.getNumSegundos() < 0 || cancion.getNumSegundos() > 59) {
            return false;
        }
        if (cancion.getNumMinutos() < 0 || cancion.getNumMinutos() > 59) {
            return false;
        }
        return cancion.getNumSegundos() != 0 || cancion.getNumMinutos() != 0;
    }


    public Boolean agregarCancion(EntityCancion cancion) {

        if (!validacionesAgregarCancion(cancion)) {
            return Boolean.FALSE;
        } else {
            String aux = "" + cancion.getNumMinutos() + ":";
            if(cancion.getNumSegundos() < 10){
                aux += "0" + cancion.getNumSegundos();
            }
            else{
                aux += cancion.getNumSegundos();
            }
            String titulo = cancion.getTitulo();
            String autor = cancion.getAutor();
            String genero = cancion.getGenero();
            Long minutos = cancion.getNumMinutos();
            Long segundos = cancion.getNumSegundos();
            Integer posicion = listarCanciones().size() + 1;
            repositoryPlayList.save(new EntityCancion(titulo, autor, genero, minutos, segundos, aux ,posicion));
        }
        return true;
    }

    public Boolean eliminarCancion(EntityCancion cancion) {

        try {
            Optional<EntityCancion> buscada = repositoryPlayList.findById(cancion.getId());
            if(buscada.isPresent()){
                repositoryPlayList.deleteById(buscada.get().getId());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean validarFormatoTiempo(String tiempo) {
        /*
        Verificar que el string tiene el formato esperado la d se refiere a formato decimal,
        la sección de minutos acepta 1 o dos dígitos, la de segundos acepta únicamente 2 dígitos
         */
        if (!tiempo.matches("\\d{1,2}:\\d{2}")) {
            return false;
        }

        // Extraer los valores de minutos y segundos
        String[] partes = tiempo.split(":");
        int minutos = Integer.parseInt(partes[0]);
        int segundos = Integer.parseInt(partes[1]);

        // Verificar que los valores de minutos y segundos están dentro del rango válido
        if (minutos < 0 || minutos > 59 || segundos < 0 || segundos > 59) {
            return false;
        }

        return true;
    }

    public Boolean subirCancion(EntityCancion cancion) {

        Optional<EntityCancion> buscado = repositoryPlayList.findById(cancion.getId());
        EntityCancion aReemplazar = repositoryPlayList.findByPosicion(cancion.getPosicion() - 1);

        if (buscado.isPresent()) {
            buscado.get().setPosicion(cancion.getPosicion() - 1);
            aReemplazar.setPosicion(cancion.getPosicion() + 1);

            repositoryPlayList.save(buscado.get());
            repositoryPlayList.save(aReemplazar);

            return true;
        }
        return false;

    }

    public Boolean bajarCancion(EntityCancion cancion) {

        Optional<EntityCancion> buscado = repositoryPlayList.findById(cancion.getId());
        EntityCancion aReemplazar = repositoryPlayList.findByPosicion(cancion.getPosicion() + 1);

        if (buscado.isPresent()) {
            buscado.get().setPosicion(cancion.getPosicion() + 1);
            aReemplazar.setPosicion(cancion.getPosicion() - 1);

            repositoryPlayList.save(buscado.get());
            repositoryPlayList.save(aReemplazar);

            return true;
        }
        return false;

    }


    public Boolean editarCancion(EntityCancion pCancion) {
        try {
            Optional<EntityCancion> buscado = repositoryPlayList.findById(pCancion.getId());
            if (buscado.isPresent()) {
                EntityCancion cancion = buscado.get();
                if (!validarFormatoTiempo(pCancion.getDuracion())) {
                    return false;
                }
                cancion.setDuracion(pCancion.getDuracion());
                cancion.setTitulo(pCancion.getTitulo());
                cancion.setAutor(pCancion.getAutor());
                cancion.setGenero(pCancion.getGenero());
                repositoryPlayList.save(cancion);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<EntityCancion> listarCanciones() {
        return repositoryPlayList.findAll();
    }


    public List<EntityCancion> darCancionesOrdenadas() {
        return repositoryPlayList.findByOrderByPosicionAsc();
    }
}
