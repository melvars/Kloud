import com.fizzed.rocker.RenderingException;
import com.fizzed.rocker.runtime.DefaultRockerTemplate;
import com.fizzed.rocker.runtime.PlainTextUnloadedClassLoader;

import java.io.IOException;

/*
 * Auto generated code to render template /fileview.rocker.html
 * Do not edit this file. Changes will eventually be overwritten by Rocker parser!
 */
@SuppressWarnings("unused")
public class fileview extends com.fizzed.rocker.runtime.DefaultRockerModel {

    static public com.fizzed.rocker.ContentType getContentType() {
        return com.fizzed.rocker.ContentType.HTML;
    }

    static public String getTemplateName() {
        return "fileview.rocker.html";
    }

    static public String getTemplatePackageName() {
        return "";
    }

    static public String getHeaderHash() {
        return "868254209";
    }

    static public long getModifiedAt() {
        return 1554408818000L;
    }

    static public String[] getArgumentNames() {
        return new String[]{"content"};
    }

    // argument @ [1:2]
    private String content;

    public fileview content(String content) {
        this.content = content;
        return this;
    }

    public String content() {
        return this.content;
    }

    static public fileview template(String content) {
        return new fileview()
                .content(content);
    }

    @Override
    protected DefaultRockerTemplate buildTemplate() throws RenderingException {
        // optimized for convenience (runtime auto reloading enabled if rocker.reloading=true)
        return com.fizzed.rocker.runtime.RockerRuntime.getInstance().getBootstrap().template(this.getClass(), this);
    }

    static public class Template extends com.fizzed.rocker.runtime.DefaultRockerTemplate {

        // <!doctype html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <meta content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"\n          name=\"viewport\">\n    <meta content=\"ie=edge\" http-equiv=\"X-UA-Compatible\">\n    <title>Fileview</title>\n</head>\n<body>\n
        static private final byte[] PLAIN_TEXT_0_0;
        // \n</body>\n</html>\n
        static private final byte[] PLAIN_TEXT_1_0;

        static {
            PlainTextUnloadedClassLoader loader = PlainTextUnloadedClassLoader.tryLoad(fileview.class.getClassLoader(), fileview.class.getName() + "$PlainText", "UTF-8");
            PLAIN_TEXT_0_0 = loader.tryGet("PLAIN_TEXT_0_0");
            PLAIN_TEXT_1_0 = loader.tryGet("PLAIN_TEXT_1_0");
        }

        // argument @ [1:2]
        protected final String content;

        public Template(fileview model) {
            super(model);
            __internal.setCharset("UTF-8");
            __internal.setContentType(getContentType());
            __internal.setTemplateName(getTemplateName());
            __internal.setTemplatePackageName(getTemplatePackageName());
            this.content = model.content();
        }

        @Override
        protected void __doRender() throws IOException, RenderingException {
            // PlainText @ [1:23]
            __internal.aboutToExecutePosInTemplate(1, 23);
            __internal.writeValue(PLAIN_TEXT_0_0);
            // ValueExpression @ [13:5]
            __internal.aboutToExecutePosInTemplate(13, 5);
            __internal.renderValue(content, false);
            // PlainText @ [13:13]
            __internal.aboutToExecutePosInTemplate(13, 13);
            __internal.writeValue(PLAIN_TEXT_1_0);
        }
    }

    private static class PlainText {

        static private final String PLAIN_TEXT_0_0 = "<!doctype html>\n<html lang=\"en\">\n<head>\n    <meta charset=\"UTF-8\">\n    <meta content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\"\n          name=\"viewport\">\n    <meta content=\"ie=edge\" http-equiv=\"X-UA-Compatible\">\n    <title>Fileview</title>\n</head>\n<body>\n    ";
        static private final String PLAIN_TEXT_1_0 = "\n</body>\n</html>\n";

    }

}
