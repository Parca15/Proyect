package Funcionalidades;

import EstructurasDatos.Cola;
import EstructurasDatos.ListaEnlazada;
import EstructurasDatos.Mapa;
import EstructurasDatos.Nodo;
import ModelosBase.Actividad;
import ModelosBase.Proceso;
import ModelosBase.Tarea;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class ExcelDataHandler {
    private final GestionProcesos gestionProcesos;

    public ExcelDataHandler(GestionProcesos gestionProcesos) {
        this.gestionProcesos = gestionProcesos;
    }

    public void exportarProcesoSeleccionado(String rutaArchivo, UUID idProceso) throws IOException {
        Proceso procesoSeleccionado = gestionProcesos.getProcesos().get(idProceso);
        if (procesoSeleccionado == null) {
            throw new IllegalArgumentException("No se encontró el proceso con ID: " + idProceso);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            // Crear hoja para Proceso
            Sheet sheetProceso = workbook.createSheet("Proceso");
            crearEncabezadosProcesos(sheetProceso);
            llenarDatosProcesoSeleccionado(sheetProceso, procesoSeleccionado);

            // Crear hoja para Actividades
            Sheet sheetActividades = workbook.createSheet("Actividades");
            crearEncabezadosActividades(sheetActividades);
            llenarDatosActividadesDelProceso(sheetActividades, procesoSeleccionado);

            // Crear hoja para Tareas
            Sheet sheetTareas = workbook.createSheet("Tareas");
            crearEncabezadosTareas(sheetTareas);
            llenarDatosTareasDelProceso(sheetTareas, procesoSeleccionado);

            // Guardar el archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
        }
    }

    private void llenarDatosProcesoSeleccionado(Sheet sheet, Proceso proceso) {
        Row row = sheet.createRow(1);

        row.createCell(0).setCellValue(proceso.getId().toString());
        row.createCell(1).setCellValue(proceso.getNombre());

        Cell fechaCell = row.createCell(2);
        fechaCell.setCellValue(
                Date.from(proceso.getFechaInicio().atZone(ZoneId.systemDefault()).toInstant())
        );
        CellStyle dateStyle = sheet.getWorkbook().createCellStyle();
        dateStyle.setDataFormat(
                sheet.getWorkbook().createDataFormat().getFormat("dd/mm/yyyy hh:mm")
        );
        fechaCell.setCellStyle(dateStyle);
    }

    private void llenarDatosActividadesDelProceso(Sheet sheet, Proceso proceso) {
        int rowNum = 1;
        ListaEnlazada<Actividad> actividades = proceso.getActividades();

        for (int i = 0; i < actividades.getTamanio(); i++) {
            Actividad actividad = actividades.getElementoEnPosicion(i);
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(UUID.randomUUID().toString());
            row.createCell(1).setCellValue(proceso.getId().toString());
            row.createCell(2).setCellValue(actividad.getNombre());
            row.createCell(3).setCellValue(actividad.getDescripcion());
            row.createCell(4).setCellValue(actividad.isObligatoria());

            Cell fechaCell = row.createCell(5);
            fechaCell.setCellValue(
                    Date.from(actividad.getFechaInicio().atZone(ZoneId.systemDefault()).toInstant())
            );
            CellStyle dateStyle = sheet.getWorkbook().createCellStyle();
            dateStyle.setDataFormat(
                    sheet.getWorkbook().createDataFormat().getFormat("dd/mm/yyyy hh:mm")
            );
            fechaCell.setCellStyle(dateStyle);
        }
    }

    private void llenarDatosTareasDelProceso(Sheet sheet, Proceso proceso) {
        int rowNum = 1;
        ListaEnlazada<Actividad> actividades = proceso.getActividades();

        for (int i = 0; i < actividades.getTamanio(); i++) {
            Actividad actividad = actividades.getElementoEnPosicion(i);
            Cola<Tarea> tareas = actividad.getTareas();
            Nodo<Tarea> actual = tareas.getNodoPrimero();

            while (actual != null) {
                Tarea tarea = actual.getValorNodo();
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(UUID.randomUUID().toString());
                row.createCell(1).setCellValue(actividad.getNombre());
                row.createCell(2).setCellValue(tarea.getDescripcion());
                row.createCell(3).setCellValue(tarea.getDuracion());
                row.createCell(4).setCellValue(tarea.isObligatoria());

                Cell fechaCell = row.createCell(5);
                fechaCell.setCellValue(
                        Date.from(tarea.getFechaCreacion().atZone(ZoneId.systemDefault()).toInstant())
                );
                CellStyle dateStyle = sheet.getWorkbook().createCellStyle();
                dateStyle.setDataFormat(
                        sheet.getWorkbook().createDataFormat().getFormat("dd/mm/yyyy hh:mm")
                );
                fechaCell.setCellStyle(dateStyle);

                actual = actual.getSiguienteNodo();
            }
        }
    }



    private void crearEncabezadosProcesos(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nombre");
        headerRow.createCell(2).setCellValue("Fecha Inicio");
    }

    private void crearEncabezadosActividades(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");  // Cambiado de "ID Proceso" a "ID"
        headerRow.createCell(1).setCellValue("ID Proceso");  // Añadido nuevo campo
        headerRow.createCell(2).setCellValue("Nombre");
        headerRow.createCell(3).setCellValue("Descripción");
        headerRow.createCell(4).setCellValue("Obligatoria");
        headerRow.createCell(5).setCellValue("Fecha Inicio");
    }

    private void crearEncabezadosTareas(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");  // Añadido ID único para tareas
        headerRow.createCell(1).setCellValue("ID Actividad");
        headerRow.createCell(2).setCellValue("Descripción");
        headerRow.createCell(3).setCellValue("Duración");
        headerRow.createCell(4).setCellValue("Obligatoria");
        headerRow.createCell(5).setCellValue("Fecha Creación");
    }

    public void importarDatos(String rutaArchivo) throws IOException {
        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Importar Procesos primero
            Mapa<String, Proceso> procesosImportados = importarProcesos(workbook.getSheet("Proceso"));

            // Importar Actividades y asociarlas a los procesos
            importarActividadesYAsociar(workbook.getSheet("Actividades"), procesosImportados);

            // Importar Tareas y asociarlas a las actividades correspondientes
            importarTareasYAsociar(workbook.getSheet("Tareas"), procesosImportados);

            // Actualizar los procesos en la gestión
            for (Proceso proceso : procesosImportados) {
                gestionProcesos.getProcesos().put(proceso.getId(), proceso);
            }
        }
    }

    private Mapa<String, Proceso> importarProcesos(Sheet sheet) {
        Mapa<String, Proceso> procesosImportados = new Mapa<>();
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Saltar fila de encabezados

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String id = row.getCell(0).getStringCellValue();
            String nombre = row.getCell(1).getStringCellValue();
            Date fechaInicio = row.getCell(2).getDateCellValue();

            Proceso proceso = new Proceso(nombre);
            proceso.setId(UUID.fromString(id)); // Mantener el mismo ID del archivo
            proceso.setFechaInicio(LocalDateTime.ofInstant(
                    fechaInicio.toInstant(),
                    ZoneId.systemDefault()
            ));
            procesosImportados.put(id, proceso);
        }

        return procesosImportados;
    }

    private void importarActividadesYAsociar(Sheet sheet, Mapa<String, Proceso> procesosImportados) {
        if (sheet == null) return;

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Saltar fila de encabezados

        Mapa<String, Actividad> actividadesImportadas = new Mapa<>();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String idActividad = row.getCell(0).getStringCellValue();
            String idProceso = row.getCell(1).getStringCellValue();
            String nombre = row.getCell(2).getStringCellValue();
            String descripcion = row.getCell(3).getStringCellValue();
            boolean obligatoria = row.getCell(4).getBooleanCellValue();
            Date fechaInicio = row.getCell(5).getDateCellValue();

            Actividad actividad = new Actividad(nombre, descripcion, obligatoria);
            actividad.setFechaInicio(LocalDateTime.ofInstant(
                    fechaInicio.toInstant(),
                    ZoneId.systemDefault()
            ));
            actividadesImportadas.put(idActividad, actividad);

            // Asociar la actividad al proceso correspondiente
            Proceso proceso = procesosImportados.get(idProceso);
            if (proceso != null) {
                proceso.agregarActividad(actividad);
            }
        }
    }

    private void importarTareasYAsociar(Sheet sheet, Mapa<String, Proceso> procesosImportados) {
        if (sheet == null) return;

        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Saltar fila de encabezados

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String idTarea = row.getCell(0).getStringCellValue();
            String nombreActividad = row.getCell(1).getStringCellValue();
            String descripcion = row.getCell(2).getStringCellValue();
            int duracion = (int) row.getCell(3).getNumericCellValue();
            boolean obligatoria = row.getCell(4).getBooleanCellValue();
            Date fechaCreacion = row.getCell(5).getDateCellValue();

            Tarea tarea = new Tarea(descripcion, duracion, obligatoria);
            tarea.setFechaCreacion(LocalDateTime.ofInstant(
                    fechaCreacion.toInstant(),
                    ZoneId.systemDefault()
            ));

            // Buscar la actividad correspondiente y asociar la tarea
            for (Proceso proceso : procesosImportados) {
                ListaEnlazada<Actividad> actividades = proceso.getActividades();
                for (int i = 0; i < actividades.getTamanio(); i++) {
                    Actividad actividad = actividades.getElementoEnPosicion(i);
                    if (actividad.getNombre().equals(nombreActividad)) {
                        actividad.agregarTarea(tarea);
                        break;
                    }
                }
            }
        }
    }



}