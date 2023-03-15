import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AudioCaptcha {
    private static final int CAPTCHA_LENGTH = 3;
    private static final int[] NOTE_VALUES = {60, 62, 64, 65, 67, 69, 71, 72}; // C4, D4, E4, F4, G4, A4, B4, C5

    public String generateCaptcha() {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append((char) (random.nextInt(8) + 'A'));
        }
        return captcha.toString();
    }

    public int getHighestFrequencyNotePosition(String captcha) {
        int highestFrequencyNote = -1;
        int highestFrequencyNotePosition = -1;

        for (int i = 0; i < captcha.length(); i++) {
            char note = captcha.charAt(i);
            int noteValue = NOTE_VALUES[note - 'A'];

            if (noteValue > highestFrequencyNote) {
                highestFrequencyNote = noteValue;
                highestFrequencyNotePosition = i + 1;
            }
        }

        return highestFrequencyNotePosition;
    }

    public void generateAudio(String captcha, File outputFile) throws MidiUnavailableException, IOException, InvalidMidiDataException {
        Sequence sequence = new Sequence(Sequence.PPQ, 4);
        Track track = sequence.createTrack();

        int tick = 0;
        for (char note : captcha.toCharArray()) {
            int noteValue = NOTE_VALUES[note - 'A'];
            track.add(createNoteOnEvent(noteValue, tick));
            tick += 4;
            track.add(createNoteOffEvent(noteValue, tick));
            tick += 4;
        }

        MidiSystem.write(sequence, 1, outputFile);
    }

    private MidiEvent createNoteOnEvent(int note, long tick) throws InvalidMidiDataException {
        return createNoteEvent(ShortMessage.NOTE_ON, note, 93, tick);
    }

    private MidiEvent createNoteOffEvent(int note, long tick) throws InvalidMidiDataException {
        return createNoteEvent(ShortMessage.NOTE_OFF, note, 0, tick);
    }

    private MidiEvent createNoteEvent(int command, int note, int velocity, long tick) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(command, 0, note, velocity);
        return new MidiEvent(message, tick);
    }
}
