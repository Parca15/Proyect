package Funcionalidades;

import EstructurasDatos.Cola;
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

    public void exportarDatos(String rutaArchivo) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Crear hoja para Procesos
            Sheet sheetProcesos = workbook.createSheet("Procesos");
            crearEncabezadosProcesos(sheetProcesos);
            llenarDatosProcesos(sheetProcesos);

            // Crear hoja para Actividades
            Sheet sheetActividades = workbook.createSheet("Actividades");
            crearEncabezadosActividades(sheetActividades);
            llenarDatosActividades(sheetActividades);

            // Crear hoja para Tareas
            Sheet sheetTareas = workbook.createSheet("Tareas");
            crearEncabezadosTareas(sheetTareas);
            llenarDatosTareas(sheetTareas);

            // Guardar el archivo
            try (FileOutputStream fileOut = new FileOutputStream(rutaArchivo)) {
                workbook.write(fileOut);
            }
        }
    }

    public void importarDatos(String rutaArchivo) throws IOException {
        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Importar Procesos
            Map<String, Proceso> procesosImportados = importarProcesos(workbook.getSheet("Procesos"));

            // Importar Actividades
            Map<String, Actividad> actividadesImportadas = importarActividades(workbook.getSheet("Actividades"));

            // Importar Tareas y asociarlas a las actividades
            importarTareas(workbook.getSheet("Tareas"), actividadesImportadas);

            // Asociar actividades a procesos
            asociarActividadesAProcesos(procesosImportados, actividadesImportadas);
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

    private void llenarDatosProcesos(Sheet sheet) {
        int rowNum = 1;
        for (Map.Entry<UUID, Proceso> entry : gestionProcesos.getProcesos().entrySet()) {
            Proceso proceso = entry.getValue();
            Row row = sheet.createRow(rowNum++);

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
    }

    private void llenarDatosActividades(Sheet sheet) {
        int rowNum = 1;
        for (Proceso proceso : gestionProcesos.getProcesos().values()) {
            for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(UUID.randomUUID().toString());  // ID único para la actividad
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
    }

    private void llenarDatosTareas(Sheet sheet) {
        int rowNum = 1;
        for (Proceso proceso : gestionProcesos.getProcesos().values()) {
            for (int i = 0; i < proceso.getActividades().getTamanio(); i++) {
                Actividad actividad = proceso.getActividades().getElementoEnPosicion(i);

                // Obtener las tareas de la actividad usando la cola
                Cola<Tarea> tareas = actividad.getTareas();
                Nodo<Tarea> actual = tareas.getNodoPrimero();

                while (actual != null) {
                    Tarea tarea = actual.getValorNodo();
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0).setCellValue(UUID.randomUUID().toString());  // ID único para la tarea
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
    }

    private Map<String, Proceso> importarProcesos(Sheet sheet) {
        Map<String, Proceso> procesosImportados = new HashMap<>();
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip header row

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String id = row.getCell(0).getStringCellValue();
            String nombre = row.getCell(1).getStringCellValue();
            Date fechaInicio = row.getCell(2).getDateCellValue();

            Proceso proceso = gestionProcesos.crearProceso(nombre);
            proceso.setFechaInicio(LocalDateTime.ofInstant(
                    fechaInicio.toInstant(),
                    ZoneId.systemDefault()
            ));
            procesosImportados.put(id, proceso);
        }

        return procesosImportados;
    }

    private Map<String, Actividad> importarActividades(Sheet sheet) {
        Map<String, Actividad> actividadesImportadas = new HashMap<>();
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip header row

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String id = row.getCell(0).getStringCellValue();
            String nombre = row.getCell(2).getStringCellValue();
            String descripcion = row.getCell(3).getStringCellValue();
            boolean obligatoria = row.getCell(4).getBooleanCellValue();
            Date fechaInicio = row.getCell(5).getDateCellValue();

            Actividad actividad = new Actividad(nombre, descripcion, obligatoria);
            actividad.setFechaInicio(LocalDateTime.ofInstant(
                    fechaInicio.toInstant(),
                    ZoneId.systemDefault()
            ));
            actividadesImportadas.put(id, actividad);
        }

        return actividadesImportadas;
    }

    private void importarTareas(Sheet sheet, Map<String, Actividad> actividadesImportadas) {
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next(); // Skip header row

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            String idActividad = row.getCell(1).getStringCellValue();
            String descripcion = row.getCell(2).getStringCellValue();
            int duracion = (int) row.getCell(3).getNumericCellValue();
            boolean obligatoria = row.getCell(4).getBooleanCellValue();
            Date fechaCreacion = row.getCell(5).getDateCellValue();

            Actividad actividad = actividadesImportadas.get(idActividad);
            if (actividad != null) {
                Tarea tarea = new Tarea(descripcion, duracion, obligatoria);
                tarea.setFechaCreacion(LocalDateTime.ofInstant(
                        fechaCreacion.toInstant(),
                        ZoneId.systemDefault()
                ));
                actividad.agregarTarea(tarea);
            }
        }
    }

    private void asociarActividadesAProcesos(
            Map<String, Proceso> procesosImportados,
            Map<String, Actividad> actividadesImportadas) {
        // Crear un mapa temporal para asociar actividades con sus procesos
        Map<String, List<Actividad>> actividadesPorProceso = new HashMap<>();

        // Agrupar actividades por proceso
        for (Map.Entry<String, Actividad> entry : actividadesImportadas.entrySet()) {
            String idProceso = entry.getValue().getDescripcion();
            actividadesPorProceso
                    .computeIfAbsent(idProceso, k -> new ArrayList<>())
                    .add(entry.getValue());
        }

        // Asociar actividades a sus respectivos procesos
        for (Map.Entry<String, List<Actividad>> entry : actividadesPorProceso.entrySet()) {
            Proceso proceso = procesosImportados.get(entry.getKey());
            if (proceso != null) {
                for (Actividad actividad : entry.getValue()) {
                    proceso.agregarActividad(actividad);
                }
            }
        }
    }
}