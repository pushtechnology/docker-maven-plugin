package com.alexecollins.docker.mojo;

import com.alexecollins.docker.orchestration.DockerOrchestrator;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.VersionCmd;
import com.github.dockerjava.api.model.Version;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.apache.maven.model.Build;
import org.apache.maven.project.MavenProject;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.io.File;
import java.util.Properties;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@PrepareForTest(DockerClientBuilder.class)
public class MojoTestSupport {

    protected static final String PROJECT_GROUP_ID = "id.group";

    protected static final String PROJECT_ARTIFACT_ID = "artifact-id";

    protected static final String PROJECT_VERSION = "1.2.3-SNAPSHOT";

    protected static final String PROJECT_NAME = "project-name";

    protected static final String PROJECT_DESCRIPTION = "Project Description";

    protected static final String BUILD_DIR = "/build/dir";

    protected static final String BUILD_FINAL_NAME = "buildFinalName";

    protected static final String BASE_DIR = "src";

    protected static final String SRC = "main";

    protected static final String PREFIX = "prefix";

    protected static final String USERNAME = "username";


    protected void prepareMojo(
            AbstractDockerMojo dockerMojo,
            DockerClient mockDockerClient,
            DockerOrchestrator mockDockerOrchestrator) throws Exception {

        MavenProject mavenProject = createMavenProject();
        Whitebox.setInternalState(dockerMojo, "project", mavenProject);
        Whitebox.setInternalState(dockerMojo, "prefix", PREFIX);
        Whitebox.setInternalState(dockerMojo, "src", SRC);
        Whitebox.setInternalState(dockerMojo, "username", USERNAME);

        // prepare docker client
        if (mockDockerClient != null) {
            VersionCmd mockVersionCmd = mock(VersionCmd.class);
            Version mockVersion = mock(Version.class);

            DockerClientBuilder mockDockerClientBuilder = mock(DockerClientBuilder.class);
            when(mockDockerClientBuilder.build()).thenReturn(mockDockerClient);

            mockStatic(DockerClientBuilder.class);
            when(DockerClientBuilder.getInstance(any(DockerClientConfig.class))).thenReturn(mockDockerClientBuilder);

            when(mockDockerClient.versionCmd()).thenReturn(mockVersionCmd);
            when(mockVersionCmd.exec()).thenReturn(mockVersion);
            when(mockVersion.getVersion()).thenReturn("1.0");
        }

        // prepare docker orchestrator
        if (mockDockerOrchestrator != null) {
            PowerMockito.doReturn(mockDockerOrchestrator)
                    .when(dockerMojo, "dockerOrchestrator",
                            any(Properties.class),
                            any(DockerClient.class)
                    );
        }
    }

    protected MavenProject createMavenProject() {

        MavenProject mavenProject = new MavenProject();
        mavenProject.setGroupId(PROJECT_GROUP_ID);
        mavenProject.setArtifactId(PROJECT_ARTIFACT_ID);
        mavenProject.setVersion(PROJECT_VERSION);
        mavenProject.setName(PROJECT_NAME);
        mavenProject.setDescription(PROJECT_DESCRIPTION);
        mavenProject.setBuild(createBuild());

        mavenProject.setFile(new File(new File(BASE_DIR), "file"));

        return mavenProject;
    }

    protected Build createBuild() {

        Build build = new Build();

        build.setDirectory(BUILD_DIR);
        build.setFinalName(BUILD_FINAL_NAME);

        return build;
    }
}
