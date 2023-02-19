package app.gpx_animator.ui.swing;

import app.gpx_animator.core.preferences.Preferences;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.FileAppender;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class LogViewerDialog extends EscapeDialog {

    @Serial
    private static final long serialVersionUID = 5563604480066875446L;

    private static final int TEXT_SIZE = 14;

    public LogViewerDialog(final JFrame owner) {
        super(owner);

        final var resourceBundle = Preferences.getResourceBundle();

        setTitle(resourceBundle.getString("ui.dialog.logviewer.title"));
        setBounds(100, 100, 657, 535);
        getContentPane().setLayout(new BorderLayout());

        final var contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.LINE_AXIS));

        final var textArea = new JTextArea();
        textArea.setEditable(false);
        final var font = new Font(textArea.getFont().getName(), textArea.getFont().getStyle(), TEXT_SIZE);
        textArea.setFont(font);

        final var lines = readLogFile(resourceBundle);
        final var log = String.join("\n", lines);
        textArea.setText(log);

        final var scrollPlane = new JScrollPane(textArea);
        contentPanel.add(scrollPlane);

        final var buttonPanel = new JPanel();
        final var copyAllLogButton = new JButton(resourceBundle.getString("ui.dialog.logviewer.copyallbutton"));
        copyAllLogButton.addActionListener(e -> copyLog(lines));
        buttonPanel.add(copyAllLogButton);
        final var copy50LogButton = new JButton(resourceBundle.getString("ui.dialog.logviewer.copy50button"));
        copy50LogButton.addActionListener(e -> {
            var copyLog = lines.subList(Math.max(lines.size() - 50, 0), lines.size());
            copyLog(copyLog);
        });
        buttonPanel.add(copy50LogButton);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    }

    private static void copyLog(final List<String> log) {
        final var copyLog = String.join("\n", log);
        final var selection = new StringSelection(copyLog);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    private List<String> readLogFile(@NonNull final ResourceBundle resourceBundle) {
        final var loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        final var logbackLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        final var fileAppender = (FileAppender<?>) logbackLogger.getAppender("FILE");
        final var file = fileAppender.getFile();
        try (var lines = Files.lines(Path.of(file))) {
            final var protocol = lines.toList();
            return protocol.isEmpty() ? List.of(resourceBundle.getString("ui.dialog.logviewer.empty")) : protocol;
        } catch (IOException e) {
            throw new RuntimeException("Unable to open log file %s".formatted(file));
        }
    }
}
