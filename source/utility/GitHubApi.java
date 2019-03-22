package utility;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GitHubApi {

	public static final String USERNAME = "mgtmadness25";

	public static final String REPO_NAME = "dawntained_server";

	public static final String FILE_NAME = "source/core/ServerConstants.java";

	/**
	 * Store file name#SHA
	 */
	private static ArrayList<String> shaDatabase = new ArrayList<String>();


	/**
	 * Update the dropbox pet and item files if the Github pet/item data has updated recently.
	 */
	public static void updateDropboxFiles() throws IOException {
		Misc.print("Github check.");
		WebsiteLogInDetails.readLatestWebsiteLogInDetails();
		// Basic authentication
		GitHubClient client = new GitHubClient();
		client.setCredentials(USERNAME, WebsiteLogInDetails.GITHUB_PASSWORD);

		// first use token service
		RepositoryService repoService = new RepositoryService(client);

		try {
			Repository repo = repoService.getRepository(USERNAME, REPO_NAME);
			String itemsContent = "";
			String petsContent = "";
			itemsContent = githubFileUpdated(client, repo, "source/core/ServerConstants.java");
			//petsContent = githubFileUpdated(client, repo, "source/game/npc/pet/PetData.java");
			if (!itemsContent.isEmpty() || !petsContent.isEmpty()) {
				// Dropbox intance.
				@SuppressWarnings("deprecation")
				DbxRequestConfig config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
				DbxClientV2 dropboxClient = new DbxClientV2(config, DropboxApi.ACCESS_TOKEN);

				if (!petsContent.isEmpty()) {
					DropboxApi.updateCustomPets(dropboxClient, petsContent);
				}
				if (!itemsContent.isEmpty()) {
					DropboxApi.updateCustomItems(dropboxClient, itemsContent);
				}
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param githubFileLocation Directory of the github file on github.
	 * @return The content of the github file. It will return empty if the github file has not been updated.
	 */
	private static String githubFileUpdated(GitHubClient client, Repository repo, String githubFileLocation) throws IOException {
		// now contents service
		ContentsService contentService = new ContentsService(client);
		List<RepositoryContents> specificFile = contentService.getContents(repo, githubFileLocation);

		// Loop
		for (RepositoryContents content : specificFile) {
			String currentSha = content.getSha() + githubFileLocation;
			if (shaDatabase.contains(currentSha)) {
				return "";
			} else {
				shaDatabase.add(currentSha);
			}
			String fileConent = content.getContent();
			String valueDecoded = new String(Base64.decodeBase64(fileConent.getBytes()));
			return valueDecoded;
		}
		return "";
	}
}
