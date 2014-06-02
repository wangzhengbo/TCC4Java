int main(int argc, char *argv[]) {
	int i, ret = 0;
	for(i = 0; i < argc; i++) {
		ret += atoi(argv[i]);
	}
	
	return ret;
}