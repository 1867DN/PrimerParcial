/**
 * Trabajo Primer Parcial
 * Nombre: Leandro Nuñez
 * Legajo: 52720
 */

package PrimerParcial;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import PrimerParcial.dao.ClienteDAOImpl;
import PrimerParcial.dao.PedidoDAOImpl;
import PrimerParcial.model.Cliente;
import PrimerParcial.model.Pedido;

/**
 * Clase principal del sistema. Implementa un menú de consola para gestionar Clientes y Pedidos.
 * Utiliza el patrón DAO (Data Access Object) para el acceso a datos,
 * separando la lógica de negocio de la lógica de acceso a base de datos.
 * La base de datos (H2 embebido) se crea y gestiona automáticamente.
 */
public class App {
    // Logger de Log4j para registrar información, advertencias y errores del sistema
    private static final Logger logger = LogManager.getLogger(App.class);

    /**
     * Inicializa la base de datos creando las tablas necesarias si no existen.
     * Esta función se ejecuta automáticamente al inicio del programa.
     * No se necesita un script SQL externo.
     */
    private static void inicializarBaseDeDatos() {
        try (var conn = PrimerParcial.util.ConnectionManager.getConnection();
             var stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Cliente (id IDENTITY PRIMARY KEY, nombre VARCHAR(100), email VARCHAR(100))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Pedido (id IDENTITY PRIMARY KEY, clienteId INT, fecha DATE, total DOUBLE, FOREIGN KEY (clienteId) REFERENCES Cliente(id))");
            logger.info("Tablas creadas o ya existentes.");
        } catch (Exception e) {
            logger.error("Error creando tablas", e);
        }
    }

    /**
     * Método principal. Ejecuta el menú interactivo y controla el flujo general del sistema.
     */
    public static void main(String[] args) {
        inicializarBaseDeDatos();
        // Uso de try-with-resources para Scanner, asegura su cierre
        try (Scanner sc = new Scanner(System.in)) {
            // Instanciación de DAOs. El patrón DAO separa la lógica de datos del resto del sistema.
            ClienteDAOImpl clienteDAO = new ClienteDAOImpl();
            PedidoDAOImpl pedidoDAO = new PedidoDAOImpl();

            // Menú interactivo
            while (true) {
                System.out.println("\n1. Agregar cliente");
                System.out.println("2. Listar clientes");
                System.out.println("3. Agregar pedido");
                System.out.println("4. Listar pedidos");
                System.out.println("5. Actualizar cliente");
                System.out.println("6. Eliminar cliente");
                System.out.println("7. Actualizar pedido");
                System.out.println("8. Eliminar pedido");
                System.out.println("0. Salir / 0000 Cancelar");
                System.out.print("Opción: ");

                int op = -1;
                // Validación robusta de opción de menú (controla error si ponen letras u otros símbolos)
                while (true) {
                    try {
                        String input = sc.nextLine();
                        if (input.equals("0000")) {
                            System.out.println("Acción cancelada. Volviendo al menú principal.");
                            op = -1;
                            break;
                        }
                        op = Integer.parseInt(input);
                        if (op < 0 || op > 8) {
                            System.out.print("Ingrese un valor válido (0-8): ");
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.print("Ingrese un valor válido (0-8): ");
                    }
                }
                if (op == -1) continue; // Salta el resto del switch si fue cancelado

                try {
                    switch (op) {
                        case 1 -> {
                            // Alta de cliente
                            System.out.print("Nombre: ");
                            String nombre = sc.nextLine();
                            if (nombre.equals("0000")) {
                                System.out.println("Acción cancelada. Volviendo al menú principal.");
                                break;
                            }
                            System.out.print("Email: ");
                            String email = sc.nextLine();
                            if (email.equals("0000")) {
                                System.out.println("Acción cancelada. Volviendo al menú principal.");
                                break;
                            }
                            clienteDAO.crear(new Cliente(0, nombre, email));
                            System.out.println("Cliente agregado.");
                            logger.info("Nuevo cliente agregado: " + nombre);
                        }
                        case 2 -> {
                            // Listar clientes
                            List<Cliente> clientesList = clienteDAO.listar();
                            System.out.println("\nClientes existentes:");
                            if (clientesList.isEmpty()) {
                                System.out.println("No hay clientes registrados.");
                            } else {
                                clientesList.forEach(c ->
                                        System.out.println("ID: " + c.getId() + " | Nombre: " + c.getNombre() + " | Email: " + c.getEmail())
                                );
                            }
                        }
                        case 3 -> {
                            // Alta de pedido (sólo si hay clientes)
                            List<Cliente> clientesActuales = clienteDAO.listar();
                            System.out.println("\nClientes existentes:");
                            if (clientesActuales.isEmpty()) {
                                System.out.println("No hay clientes registrados. Debe agregar un cliente primero.");
                                break;
                            } else {
                                clientesActuales.forEach(c ->
                                        System.out.println("ID: " + c.getId() + " | Nombre: " + c.getNombre() + " | Email: " + c.getEmail())
                                );
                            }
                            int clienteIdPedido;
                            Cliente clienteParaPedido;
                            // Validación robusta para seleccionar un cliente existente
                            while (true) {
                                System.out.print("ID del cliente: ");
                                String idClienteInput = sc.nextLine();
                                if (idClienteInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    clienteIdPedido = -1;
                                    break;
                                }
                                try {
                                    clienteIdPedido = Integer.parseInt(idClienteInput);
                                    clienteParaPedido = clienteDAO.buscarPorId(clienteIdPedido);
                                    if (clienteParaPedido == null) {
                                        System.out.println("Ese ID no pertenece a ningún cliente existente. Intente de nuevo.");
                                    } else {
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Ingrese un ID válido (numérico).");
                                }
                            }
                            if (clienteIdPedido == -1) break;
                            System.out.print("Total del pedido: ");
                            double totalPedido = 0;
                            // Validación robusta para ingreso de monto total
                            while (true) {
                                String totalInput = sc.nextLine();
                                if (totalInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    clienteIdPedido = -1;
                                    break;
                                }
                                try {
                                    totalPedido = Double.parseDouble(totalInput);
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.print("Ingrese un valor numérico para el total: ");
                                }
                            }
                            if (clienteIdPedido == -1) break;
                            pedidoDAO.crear(new Pedido(0, clienteIdPedido, LocalDate.now(), totalPedido));
                            System.out.println("Pedido agregado.");
                            logger.info("Nuevo pedido creado para cliente ID: " + clienteIdPedido);
                        }
                        case 4 -> {
                            // Listar pedidos
                            List<Pedido> pedidosList = pedidoDAO.listar();
                            System.out.println("\nPedidos existentes:");
                            if (pedidosList.isEmpty()) {
                                System.out.println("No hay pedidos registrados.");
                            } else {
                                pedidosList.forEach(p ->
                                        System.out.println("PedidoID: " + p.getId() + " | ClienteID: " + p.getClienteId() +
                                                " | Fecha: " + p.getFecha() + " | Total: " + p.getTotal())
                                );
                            }
                        }
                        case 5 -> {
                            // Actualización de cliente existente (validación robusta de ID)
                            System.out.print("ID del cliente a actualizar: ");
                            int idActualizar;
                            while (true) {
                                String idInput = sc.nextLine();
                                if (idInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    idActualizar = -1;
                                    break;
                                }
                                try {
                                    idActualizar = Integer.parseInt(idInput);
                                    Cliente clienteExiste = clienteDAO.buscarPorId(idActualizar);
                                    if (clienteExiste == null) {
                                        System.out.print("Ese ID no pertenece a ningún cliente existente. Intente de nuevo: ");
                                    } else {
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.print("Ingrese un ID válido (numérico): ");
                                }
                            }
                            if (idActualizar == -1) break;
                            System.out.print("Nuevo nombre: ");
                            String nuevoNombre = sc.nextLine();
                            if (nuevoNombre.equals("0000")) {
                                System.out.println("Acción cancelada. Volviendo al menú principal.");
                                break;
                            }
                            System.out.print("Nuevo email: ");
                            String nuevoEmail = sc.nextLine();
                            if (nuevoEmail.equals("0000")) {
                                System.out.println("Acción cancelada. Volviendo al menú principal.");
                                break;
                            }
                            clienteDAO.actualizar(new Cliente(idActualizar, nuevoNombre, nuevoEmail));
                            System.out.println("Cliente actualizado.");
                            logger.info("Cliente actualizado, ID: " + idActualizar);
                        }
                        case 6 -> {
                            // Baja de cliente (si tiene pedidos asociados, controla eliminación en cascada)
                            System.out.print("ID del cliente a eliminar: ");
                            int idEliminar;
                            while (true) {
                                String idInput = sc.nextLine();
                                if (idInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    idEliminar = -1;
                                    break;
                                }
                                try {
                                    idEliminar = Integer.parseInt(idInput);
                                    Cliente clienteExiste = clienteDAO.buscarPorId(idEliminar);
                                    if (clienteExiste == null) {
                                        System.out.print("Ese ID no pertenece a ningún cliente existente. Intente de nuevo: ");
                                    } else {
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.print("Ingrese un ID válido (numérico): ");
                                }
                            }
                            if (idEliminar == -1) break;
                            List<Pedido> pedidosDelCliente = pedidoDAO.buscarPorCliente(idEliminar);
                            if (!pedidosDelCliente.isEmpty()) {
                                System.out.println("El cliente tiene los siguientes pedidos asociados:");
                                pedidosDelCliente.forEach(p -> System.out.println(
                                        "PedidoID: " + p.getId() + " - Fecha: " + p.getFecha() + " - Total: " + p.getTotal()));
                                System.out.print("¿Deseás eliminar también todos sus pedidos? (s/n): ");
                                String confirm = sc.nextLine();
                                if (confirm.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    break;
                                }
                                if (confirm.equalsIgnoreCase("s")) {
                                    pedidoDAO.eliminarPorCliente(idEliminar);
                                    clienteDAO.eliminar(idEliminar);
                                    System.out.println("Pedidos y cliente eliminados.");
                                    logger.info("Pedidos y cliente eliminados, ID cliente: " + idEliminar);
                                } else {
                                    System.out.println("No se eliminó nada.");
                                }
                            } else {
                                clienteDAO.eliminar(idEliminar);
                                System.out.println("Cliente eliminado.");
                                logger.info("Cliente eliminado, ID: " + idEliminar);
                            }
                        }
                        case 7 -> {
                            // Actualización de pedido. Valida existencia de pedido y de cliente asociado.
                            System.out.print("ID del pedido a actualizar: ");
                            int idPedidoActualizar;
                            Pedido pedidoAEditar;
                            while (true) {
                                String idInput = sc.nextLine();
                                if (idInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    idPedidoActualizar = -1;
                                    break;
                                }
                                try {
                                    idPedidoActualizar = Integer.parseInt(idInput);
                                    pedidoAEditar = pedidoDAO.buscarPorId(idPedidoActualizar);
                                    if (pedidoAEditar == null) {
                                        System.out.print("Ese ID no pertenece a ningún pedido existente. Intente de nuevo: ");
                                    } else {
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.print("Ingrese un ID válido (numérico): ");
                                }
                            }
                            if (idPedidoActualizar == -1) break;
                            List<Cliente> clientesExistentes = clienteDAO.listar();
                            System.out.println("\nClientes existentes:");
                            if (clientesExistentes.isEmpty()) {
                                System.out.println("No hay clientes registrados. No se puede actualizar el pedido.");
                                break;
                            } else {
                                clientesExistentes.forEach(c ->
                                        System.out.println("ID: " + c.getId() + " | Nombre: " + c.getNombre() + " | Email: " + c.getEmail())
                                );
                            }
                            int nuevoClienteId;
                            Cliente clienteActualizado;
                            while (true) {
                                System.out.print("Nuevo ID de cliente: ");
                                String idClienteInput = sc.nextLine();
                                if (idClienteInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    nuevoClienteId = -1;
                                    break;
                                }
                                try {
                                    nuevoClienteId = Integer.parseInt(idClienteInput);
                                    clienteActualizado = clienteDAO.buscarPorId(nuevoClienteId);
                                    if (clienteActualizado == null) {
                                        System.out.println("Ese ID no pertenece a ningún cliente existente. Intente de nuevo.");
                                    } else {
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.print("Ingrese un ID válido (numérico): ");
                                }
                            }
                            if (nuevoClienteId == -1) break;
                            LocalDate fechaPedido;
                            while (true) {
                                System.out.print("Nueva fecha (AAAA-MM-DD): ");
                                String fechaInput = sc.nextLine();
                                if (fechaInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    fechaPedido = null;
                                    break;
                                }
                                try {
                                    fechaPedido = LocalDate.parse(fechaInput);
                                    break;
                                } catch (Exception e) {
                                    System.out.println("Formato inválido. Debe ingresar la fecha en formato AAAA-MM-DD (ejemplo: 2025-06-03).");
                                }
                            }
                            if (fechaPedido == null) break;
                            System.out.print("Nuevo total: ");
                            double nuevoTotal;
                            while (true) {
                                String totalInput = sc.nextLine();
                                if (totalInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    nuevoTotal = -1;
                                    break;
                                }
                                try {
                                    nuevoTotal = Double.parseDouble(totalInput);
                                    break;
                                } catch (NumberFormatException e) {
                                    System.out.print("Ingrese un valor numérico para el total: ");
                                }
                            }
                            if (nuevoTotal == -1) break;
                            pedidoDAO.actualizar(new Pedido(idPedidoActualizar, nuevoClienteId, fechaPedido, nuevoTotal));
                            System.out.println("Pedido actualizado.");
                            logger.info("Pedido actualizado, ID: " + idPedidoActualizar);
                        }
                        case 8 -> {
                            // Baja de pedido. Solo permite eliminar pedidos que existen.
                            System.out.print("ID del pedido a eliminar: ");
                            int idPedidoEliminar;
                            Pedido pedidoAEliminar;
                            while (true) {
                                String idInput = sc.nextLine();
                                if (idInput.equals("0000")) {
                                    System.out.println("Acción cancelada. Volviendo al menú principal.");
                                    idPedidoEliminar = -1;
                                    break;
                                }
                                try {
                                    idPedidoEliminar = Integer.parseInt(idInput);
                                    pedidoAEliminar = pedidoDAO.buscarPorId(idPedidoEliminar);
                                    if (pedidoAEliminar == null) {
                                        System.out.print("Ese ID no pertenece a ningún pedido existente. Intente de nuevo: ");
                                    } else {
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.print("Ingrese un ID válido (numérico): ");
                                }
                            }
                            if (idPedidoEliminar == -1) break;
                            pedidoDAO.eliminar(idPedidoEliminar);
                            System.out.println("Pedido eliminado.");
                            logger.info("Pedido eliminado, ID: " + idPedidoEliminar);
                        }
                        case 0 -> {
                            // Salir del sistema
                            logger.info("Saliendo del sistema.");
                            System.out.println("¡Chau!");
                            return;
                        }
                        default -> System.out.println("Opción inválida.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    logger.error("Error en operación de menú", e);
                }
            }
        }
    }
}