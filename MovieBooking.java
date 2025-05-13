import java.util.Scanner;

class MovieBooking {
    private static final int MAX_BOOKINGS = 100;    // Maximum total bookings allowed
    private static final double PREMIUM_PRICE = 15.0; // Price for premium seats
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Constants
        final int ROWS = 5;          // Number of rows in the theater
        final int COLS = 10;         // Number of columns (seats per row)
        final int MAX_BOOKINGS = 50; // Maximum number of bookings allowed
        final String ADMIN_PASSWORD = "admin123"; // Admin login password
        final double REGULAR_PRICE = 10.0; // Price for regular seats (rows A-C)
        final double PREMIUM_PRICE = 15.0; // Price for premium seats (rows D-E)

        // Seating grid: 'A' = Available, 'B' = Booked
        char[][] seats = new char[ROWS][COLS];
        // Seat types: 'R' = Regular, 'P' = Premium (last two rows)
        char[][] seatTypes = new char[ROWS][COLS];
        // Initialize seats and types
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                seats[i][j] = 'A'; // All seats available initially
                seatTypes[i][j] = (i >= ROWS - 2) ? 'P' : 'R'; // Last two rows are Premium
            }
        }

        // Arrays to store booking details
        int[] bookedRows = new int[MAX_BOOKINGS];
        int[] bookedCols = new int[MAX_BOOKINGS];
        String[] customerNames = new String[MAX_BOOKINGS];
        String[] customerPhones = new String[MAX_BOOKINGS];
        double[] bookingPrices = new double[MAX_BOOKINGS]; // Store price per ticket
        int bookingCount = 0; // Tracks the number of bookings

        // Main program loop
        while (true) {
            System.out.println("\n=== Movie Ticket Booking System ===");
            System.out.println("1. User Mode");
            System.out.println("2. Admin Mode");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice;
            try {
                choice = scanner.nextInt();
            } catch (Exception e) {
                scanner.nextLine(); // Clear invalid input
                System.out.println("Please enter a number!");
                continue;
            }
            scanner.nextLine(); // Clear buffer

            if (choice == 3) {
                System.out.println("Thank you for using the system!");
                break;
            }

            // User Mode
            if (choice == 1) {
                while (true) {
                    System.out.println("\n--- User Mode ---");
                    System.out.println("1. View Available Seats");
                    System.out.println("2. Book Tickets");
                    System.out.println("3. View My Bookings");
                    System.out.println("4. Back to Main Menu");
                    System.out.print("Enter choice: ");
                    int userChoice;
                    try {
                        userChoice = scanner.nextInt();
                    } catch (Exception e) {
                        scanner.nextLine();
                        System.out.println("Please enter a number!");
                        continue;
                    }
                    scanner.nextLine();

                    if (userChoice == 4) {
                        break; // Return to main menu
                    }

                    switch (userChoice) {
                        case 1: // View Available Seats
                            displaySeats(seats, ROWS, COLS);
                            break;
                        case 2: // Book Tickets
                            if (bookingCount >= MAX_BOOKINGS) {
                                System.out.println("Booking limit reached!");
                                break;
                            }
                            int seatsBooked = bookTickets(scanner, seats, ROWS, COLS, seatTypes, bookedRows, bookedCols, customerNames, customerPhones, bookingPrices, bookingCount, REGULAR_PRICE, PREMIUM_PRICE);
                            bookingCount += seatsBooked;
                            break;
                        case 3: // View My Bookings
                            viewMyBookings(scanner, bookedRows, bookedCols, customerNames, customerPhones, bookingPrices, bookingCount);
                            break;
                        default:
                            System.out.println("Invalid choice!");
                    }
                }
            }
            // Admin Mode
            else if (choice == 2) {
                System.out.print("Enter admin password: ");
                String password = scanner.nextLine();
                if (!password.equals(ADMIN_PASSWORD)) {
                    System.out.println("Incorrect password!");
                    continue;
                }

                while (true) {
                    System.out.println("\n--- Admin Mode ---");
                    System.out.println("1. View Available Seats");
                    System.out.println("2. View All Bookings");
                    System.out.println("3. Change Booking Details");
                    System.out.println("4. Delete Booking");
                    System.out.println("5. Back to Main Menu");
                    System.out.print("Enter choice: ");
                    int adminChoice;
                    try {
                        adminChoice = scanner.nextInt();
                    } catch (Exception e) {
                        scanner.nextLine();
                        System.out.println("Please enter a number!");
                        continue;
                    }
                    scanner.nextLine();

                    if (adminChoice == 5) {
                        break; // Return to main menu
                    }

                    switch (adminChoice) {
                        case 1: // View Available Seats
                            displaySeats(seats, ROWS, COLS);
                            break;
                        case 2: // View All Bookings
                            viewAllBookings(bookedRows, bookedCols, customerNames, customerPhones, bookingPrices, bookingCount);
                            break;
                        case 3: // Change Booking Details
                            changeBookingDetails(scanner, seats, ROWS, COLS, bookedRows, bookedCols, customerNames, customerPhones, bookingPrices, bookingCount);
                            break;
                        case 4: // Delete Booking
                            bookingCount = deleteBooking(scanner, seats, ROWS, COLS, bookedRows, bookedCols, customerNames, customerPhones, bookingPrices, bookingCount);
                            break;
                        default:
                            System.out.println("Invalid choice!");
                    }
                }
            } else {
                System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }

    // Display seating chart with '□' for available and '■' for booked
    private static void displaySeats(char[][] seats, int ROWS, int COLS) {
        System.out.println("\nSeating Chart (□ = Available, ■ = Booked):");
        System.out.print("   ");
        for (int j = 1; j <= COLS; j++) {
            System.out.print(j + " ");
        }
        System.out.println();
        for (int i = 0; i < ROWS; i++) {
            System.out.print((char) ('A' + i) + ": ");
            for (int j = 0; j < COLS; j++) {
                char displayChar = (seats[i][j] == 'A') ? '□' : '■';
                System.out.print(displayChar + " ");
            }
            System.out.println();
        }
        System.out.println("Note: Last two rows are Premium seats.");
    }

    // Book tickets
    private static int bookTickets(Scanner scanner, char[][] seats, int ROWS, int COLS, char[][] seatTypes, int[] bookedRows, int[] bookedCols, String[] customerNames, String[] customerPhones, double[] bookingPrices, int bookingCount, double REGULAR_PRICE, double PREMIUM_PRICE) {
        System.out.print("Enter number of tickets to book: ");
        int numTickets;
        try {
            numTickets = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            scanner.nextLine();
            System.out.println("Invalid number!");
            return 0;
        }
        if (numTickets < 1 || numTickets > 10 || bookingCount + numTickets > MAX_BOOKINGS) {
            System.out.println("Invalid number of tickets or booking limit exceeded!");
            return 0;
        }

        int[] tempRows = new int[numTickets];
        int[] tempCols = new int[numTickets];
        double totalCost = 0.0;

        // Collect seat selections
        for (int k = 0; k < numTickets; k++) {
            while (true) {
                System.out.print("Enter seat for ticket " + (k + 1) + " (e.g., A3): ");
                String seatInput = scanner.nextLine().toUpperCase();
                if (seatInput.length() < 2 || !Character.isLetter(seatInput.charAt(0)) || !Character.isDigit(seatInput.charAt(1))) {
                    System.out.println("Invalid seat format! Use format like A3.");
                    continue;
                }
                int row;
                try {
                    row = seatInput.charAt(0) - 'A';
                    int col = Integer.parseInt(seatInput.substring(1)) - 1;
                    if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                        System.out.println("Seat out of range!");
                        continue;
                    }
                    if (seats[row][col] == 'B') {
                        System.out.println("Seat already booked!");
                        continue;
                    }
                    tempRows[k] = row;
                    tempCols[k] = col;
                    totalCost += (seatTypes[row][col] == 'P') ? PREMIUM_PRICE : REGULAR_PRICE;
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid column number!");
                }
            }
        }

        // Collect user details
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            System.out.println("Name cannot be empty!");
            return 0;
        }
        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();
        if (phone.trim().isEmpty()) {
            System.out.println("Phone number cannot be empty!");
            return 0;
        }

        // Confirm booking
        System.out.println("\nBooking Summary:");
        for (int k = 0; k < numTickets; k++) {
            char rowLetter = (char) ('A' + tempRows[k]);
            int colNumber = tempCols[k] + 1;
            String seatType = (seatTypes[tempRows[k]][tempCols[k]] == 'P') ? "Premium" : "Regular";
            double price = (seatTypes[tempRows[k]][tempCols[k]] == 'P') ? PREMIUM_PRICE : REGULAR_PRICE;
            System.out.printf("Seat: %s%d (%s, $%.2f)\n", rowLetter, colNumber, seatType, price);
        }
        System.out.printf("Total Cost: $%.2f\n", totalCost);
        System.out.print("Confirm booking? (y/n): ");
        if (!scanner.nextLine().toLowerCase().startsWith("y")) {
            System.out.println("Booking canceled.");
            return 0;
        }

        // Finalize booking
        for (int k = 0; k < numTickets; k++) {
            int row = tempRows[k];
            int col = tempCols[k];
            seats[row][col] = 'B';
            bookedRows[bookingCount] = row;
            bookedCols[bookingCount] = col;
            customerNames[bookingCount] = name;
            customerPhones[bookingCount] = phone;
            bookingPrices[bookingCount] = (seatTypes[row][col] == 'P') ? PREMIUM_PRICE : REGULAR_PRICE;
            bookingCount++;
        }
        System.out.println("Tickets booked successfully!");
        return numTickets;
    }

    // View bookings for a specific user
    private static void viewMyBookings(Scanner scanner, int[] bookedRows, int[] bookedCols, String[] customerNames, String[] customerPhones, double[] bookingPrices, int bookingCount) {
        System.out.print("Enter your phone number: ");
        String phone = scanner.nextLine();
        System.out.println("\nYour Bookings:");
        System.out.printf("%-10s %-10s %-15s %-15s\n", "Seat", "Type", "Price", "Name");
        boolean found = false;
        double totalCost = 0.0;
        for (int i = 0; i < bookingCount; i++) {
            if (customerPhones[i].equals(phone)) {
                char rowLetter = (char) ('A' + bookedRows[i]);
                int colNumber = bookedCols[i] + 1;
                String seatType = (bookingPrices[i] == PREMIUM_PRICE) ? "Premium" : "Regular";
                System.out.printf("%-10s %-10s $%-14.2f %-15s\n", rowLetter + "" + colNumber, seatType, bookingPrices[i], customerNames[i]);
                totalCost += bookingPrices[i];
                found = true;
            }
        }
        if (!found) {
            System.out.println("No bookings found for this phone number.");
        } else {
            System.out.printf("Total Cost: $%.2f\n", totalCost);
        }
    }

    // View all bookings (admin only)
    private static void viewAllBookings(int[] bookedRows, int[] bookedCols, String[] customerNames, String[] customerPhones, double[] bookingPrices, int bookingCount) {
        if (bookingCount == 0) {
            System.out.println("\nNo bookings yet!");
            return;
        }
        System.out.println("\nAll Bookings:");
        System.out.printf("%-10s %-10s %-15s %-15s %-15s\n", "Seat", "Type", "Price", "Name", "Phone");
        double totalRevenue = 0.0;
        for (int i = 0; i < bookingCount; i++) {
            char rowLetter = (char) ('A' + bookedRows[i]);
            int colNumber = bookedCols[i] + 1;
            String seatType = (bookingPrices[i] == PREMIUM_PRICE) ? "Premium" : "Regular";
            System.out.printf("%-10s %-10s $%-14.2f %-15s %-15s\n", rowLetter + "" + colNumber, seatType, bookingPrices[i], customerNames[i], customerPhones[i]);
            totalRevenue += bookingPrices[i];
        }
        System.out.println("Total Bookings: " + bookingCount);
        System.out.printf("Total Revenue: $%.2f\n", totalRevenue);
    }

    // Change booking details (admin only)
    private static void changeBookingDetails(Scanner scanner, char[][] seats, int ROWS, int COLS, int[] bookedRows, int[] bookedCols, String[] customerNames, String[] customerPhones, double[] bookingPrices, int bookingCount) {
        System.out.print("Enter seat to change (e.g., A3): ");
        String seatInput = scanner.nextLine().toUpperCase();
        if (seatInput.length() < 2 || !Character.isLetter(seatInput.charAt(0)) || !Character.isDigit(seatInput.charAt(1))) {
            System.out.println("Invalid seat format!");
            return;
        }
        int row;
        try {
            row = seatInput.charAt(0) - 'A';
            int col = Integer.parseInt(seatInput.substring(1)) - 1;
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                System.out.println("Seat out of range!");
                return;
            }
            if (seats[row][col] != 'B') {
                System.out.println("Seat is not booked!");
                return;
            }
            int index = findBookingIndex(bookedRows, bookedCols, bookingCount, row, col);
            if (index == -1) {
                System.out.println("Booking not found!");
                return;
            }
            System.out.print("Enter new name (leave blank to keep current): ");
            String newName = scanner.nextLine();
            if (!newName.trim().isEmpty()) {
                customerNames[index] = newName;
            }
            System.out.print("Enter new phone number (leave blank to keep current): ");
            String newPhone = scanner.nextLine();
            if (!newPhone.trim().isEmpty()) {
                customerPhones[index] = newPhone;
            }
            System.out.println("Booking details updated!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid seat format!");
        }
    }

    // Delete a booking (admin only)
    private static int deleteBooking(Scanner scanner, char[][] seats, int ROWS, int COLS, int[] bookedRows, int[] bookedCols, String[] customerNames, String[] customerPhones, double[] bookingPrices, int bookingCount) {
        System.out.print("Enter seat to delete (e.g., A3): ");
        String seatInput = scanner.nextLine().toUpperCase();
        if (seatInput.length() < 2 || !Character.isLetter(seatInput.charAt(0)) || !Character.isDigit(seatInput.charAt(1))) {
            System.out.println("Invalid seat format!");
            return bookingCount;
        }
        int row;
        try {
            row = seatInput.charAt(0) - 'A';
            int col = Integer.parseInt(seatInput.substring(1)) - 1;
            if (row < 0 || row >= ROWS || col < 0 || col >= COLS) {
                System.out.println("Seat out of range!");
                return bookingCount;
            }
            if (seats[row][col] != 'B') {
                System.out.println("Seat is not booked!");
                return bookingCount;
            }
            int index = findBookingIndex(bookedRows, bookedCols, bookingCount, row, col);
            if (index == -1) {
                System.out.println("Booking not found!");
                return bookingCount;
            }
            // Shift arrays to remove the booking
            for (int i = index; i < bookingCount - 1; i++) {
                bookedRows[i] = bookedRows[i + 1];
                bookedCols[i] = bookedCols[i + 1];
                customerNames[i] = customerNames[i + 1];
                customerPhones[i] = customerPhones[i + 1];
                bookingPrices[i] = bookingPrices[i + 1];
            }
            bookingCount--;
            seats[row][col] = 'A'; // Mark seat as available
            System.out.println("Booking deleted successfully!");
            return bookingCount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid seat format!");
            return bookingCount;
        }
    }

    // Find booking index by seat position
    private static int findBookingIndex(int[] bookedRows, int[] bookedCols, int bookingCount, int row, int col) {
        for (int i = 0; i < bookingCount; i++) {
            if (bookedRows[i] == row && bookedCols[i] == col) {
                return i;
            }
        }
        return -1; // Not found
    }
}
